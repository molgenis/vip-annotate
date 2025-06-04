package org.molgenis.vcf.annotate.db.exact.format;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.VariantEncoder;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vcf.annotate.util.MappableZipFile;

public class AnnotationDbImpl<T> implements AnnotationDb<T> {
  private final MappableZipFile zipFile;
  private final AnnotationDecoder<T> annotationDecoder;

  private final VariantEncoder variantEncoder;
  private final Fury fury;

  private final ZstdDecompressCtx zstdDecompressCtxIdx;
  private ByteBuffer directByteBufferIdx;
  private VariantAltAlleleAnnotationIndex currentVariantAltAlleleAnnotationIdx;

  private final ZstdDecompressCtx zstdDecompressCtxData;
  private ByteBuffer directByteBufferData;
  private MemoryBuffer currentVariantAltAlleleAnnotationBlob;

  private String currentContig;
  private int currentPartitionId = -1;
  private boolean loadDictionary;

  public AnnotationDbImpl(MappableZipFile zipFile, AnnotationDecoder<T> annotationDecoder) {
    this.zipFile = requireNonNull(zipFile);
    this.annotationDecoder = annotationDecoder;

    this.variantEncoder = new VariantEncoder();
    this.fury = FuryFactory.createFury();

    this.zstdDecompressCtxData = new ZstdDecompressCtx();
    this.zstdDecompressCtxIdx = new ZstdDecompressCtx();
    long maxZipArchiveEntrySizeZst = 0;
    long maxZipArchiveEntrySizeIdxZst = 0;
    for (Iterator<ZipArchiveEntry> e = zipFile.getEntries(); e.hasNext(); ) {
      ZipArchiveEntry zipArchiveEntry = e.next();
      long zipArchiveEntrySize = zipArchiveEntry.getSize();
      if (zipArchiveEntry.getName().endsWith(".idx.zst")) {
        if (zipArchiveEntrySize > maxZipArchiveEntrySizeIdxZst) {
          maxZipArchiveEntrySizeIdxZst = zipArchiveEntrySize;
        }
      } else if (zipArchiveEntry.getName().endsWith(".zst")) {
        if (zipArchiveEntrySize > maxZipArchiveEntrySizeZst) {
          maxZipArchiveEntrySizeZst = zipArchiveEntrySize;
        }
      }
    }
    this.directByteBufferIdx =
        ByteBuffer.allocateDirect(Math.toIntExact(maxZipArchiveEntrySizeIdxZst));
    this.directByteBufferData =
        ByteBuffer.allocateDirect(Math.toIntExact(maxZipArchiveEntrySizeZst));
  }

  private ZstdDictDecompress loadDictionary(String contig) {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(contig + "/var/zst.dict");
    if (zipArchiveEntry == null) throw new RuntimeException();

    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    return new ZstdDictDecompress(srcByteBuffer);
  }

  private VariantAltAlleleAnnotationIndex loadAnnotationIndex(String contig, int partitionId) {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(contig + "/var/" + partitionId + ".idx.zst");
    if (zipArchiveEntry == null) return null;

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBufferIdx.clear();
    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    zstdDecompressCtxIdx.decompressDirectByteBuffer(
        directByteBufferIdx, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    //noinspection UnusedAssignment
    srcByteBuffer = null; // mark for garbage collection
    directByteBufferIdx.position(0);
    directByteBufferIdx.limit(uncompressedSize);

    MemoryBuffer memoryBuffer =
        MemoryBuffer.fromDirectByteBuffer(
            directByteBufferIdx, Math.toIntExact(zipArchiveEntry.getSize()), null);

    return fury.deserializeJavaObject(memoryBuffer, VariantAltAlleleAnnotationIndex.class);
  }

  private MemoryBuffer loadAnnotationData(String contig, int partitionId) {
    ZipArchiveEntry zipArchiveEntry = zipFile.getEntry(contig + "/var/" + partitionId + ".zst");

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBufferData.clear();
    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    zstdDecompressCtxData.decompressDirectByteBuffer(
        directByteBufferData, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    //noinspection UnusedAssignment
    srcByteBuffer = null; // mark for garbage collection
    directByteBufferData.position(0);

    return MemoryBuffer.fromDirectByteBuffer(
        directByteBufferData, Math.toIntExact(zipArchiveEntry.getSize()), null);
  }

  @Override
  public T findAnnotations(Variant variant) {
    // Partition partition = getPartition(variant)
    // if(annotationsIdx == null) return null

    String contig = variant.contig();

    int partitionId = variantEncoder.getPartitionId(variant);
    boolean contigChanged = !contig.equals(currentContig);
    boolean partitionIdChanged = partitionId != currentPartitionId;
    currentContig = contig;
    currentPartitionId = partitionId;
    if (contigChanged) {
      loadDictionary = true;
    }

    if (partitionIdChanged || contigChanged) {
      currentVariantAltAlleleAnnotationIdx = loadAnnotationIndex(contig, partitionId);
      currentVariantAltAlleleAnnotationBlob = null;
    }

    // FIXME support alternate alleles with 'N'
    for (byte altBase : variant.alt()) {
      if (altBase == 'N') {
        return null;
      }
    }

    MemoryBuffer memoryBuffer = null;
    if (currentVariantAltAlleleAnnotationIdx != null) {
      int dataOffset = currentVariantAltAlleleAnnotationIdx.findDataOffset(variant);
      if (dataOffset != -1) {
        if (currentVariantAltAlleleAnnotationBlob == null) {
          if (loadDictionary) {
            ZstdDictDecompress zstdDictDecompress = loadDictionary(contig);
            zstdDecompressCtxData.loadDict(zstdDictDecompress);
            loadDictionary = false;
          }
          currentVariantAltAlleleAnnotationBlob = loadAnnotationData(contig, partitionId);
        }

        memoryBuffer = currentVariantAltAlleleAnnotationBlob.slice(dataOffset);
      }
    }

    return memoryBuffer != null ? annotationDecoder.decode(memoryBuffer) : null;
  }

  @Override
  public void close() throws IOException {
    zstdDecompressCtxData.close();
    directByteBufferData = null; // make available for deallocation

    zstdDecompressCtxIdx.close();
    directByteBufferIdx = null;
  }
}
