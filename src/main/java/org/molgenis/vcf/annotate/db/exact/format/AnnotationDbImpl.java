package org.molgenis.vcf.annotate.db.exact.format;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.fury.Fury;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;
import org.molgenis.vcf.annotate.db.exact.formatv2.VariantAltAlleleAnnotationIndex;
import org.molgenis.vcf.annotate.util.FuryInputStream;

public class AnnotationDbImpl implements AnnotationDb {
  private final Path annotationsZip;
  private final VariantAltAlleleEncoder variantAltAlleleEncoder;
  private final Fury fury;
  private final ZipFile zipFile;
  private final ZstdDecompressCtx zstdDecompressCtx;
  private final byte[] bytes;

  private VariantAltAlleleAnnotationIndex currentVariantAltAlleleAnnotationIndex;
  private ByteBuffer directByteBuffer;
  private MemoryBuffer currentVariantAltAlleleAnnotationBlob;
  private String currentContig;
  private int currentPartitionId = -1;

  public AnnotationDbImpl(Path annotationsZip) {
    this.annotationsZip = requireNonNull(annotationsZip);
    this.variantAltAlleleEncoder = new VariantAltAlleleEncoder();
    this.fury = FuryFactory.createFury();

    try {
      // TODO benchmark .setOpenOptions(StandardOpenOption.READ, ExtendedOpenOption.DIRECT) on HDD
      this.zipFile = ZipFile.builder().setPath(annotationsZip).get();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    this.zstdDecompressCtx = new ZstdDecompressCtx();
    long maxZipArchiveEntrySize = 0;
    for (Enumeration<ZipArchiveEntry> e = zipFile.getEntries(); e.hasMoreElements(); ) {
      long zipArchiveEntrySize = e.nextElement().getSize();
      if (zipArchiveEntrySize > maxZipArchiveEntrySize) {
        maxZipArchiveEntrySize = zipArchiveEntrySize;
      }
    }
    this.bytes = new byte[Math.toIntExact(maxZipArchiveEntrySize)];
    this.directByteBuffer = ByteBuffer.allocateDirect(Math.toIntExact(maxZipArchiveEntrySize));
  }

  @Override
  public MemoryBuffer findVariant(String contig, int start, int stop, byte[] altBases) {
    VariantAltAllele variantAltAllele = new VariantAltAllele(contig, start, stop, altBases);

    int partitionId = variantAltAlleleEncoder.getPartitionId(variantAltAllele);
    if (partitionId != currentPartitionId || !contig.equals(currentContig)) {
      currentContig = contig;
      currentPartitionId = partitionId;

      ZipArchiveEntry entry = zipFile.getEntry(contig + "/var/" + partitionId + ".idx.zst");
      if (entry == null) { // no annotations exist for this partition
        currentVariantAltAlleleAnnotationIndex = null;
        currentVariantAltAlleleAnnotationBlob = null;
      } else {
        // perf: zipFile.getInputStream creates a buffered stream, but FuryInputStream is
        // already buffered
        // perf: ZstdCompressorInputStream collects unnecessary InputStreamStatistics, use
        // ZstdInputStream instead
        try (FuryInputStream furyInputStream =
            new FuryInputStream(
                new ZstdInputStreamNoFinalizer(
                    zipFile.getRawInputStream(entry), RecyclingBufferPool.INSTANCE),
                bytes)) {
          currentVariantAltAlleleAnnotationIndex =
              fury.deserializeJavaObject(
                  furyInputStream.getBuffer(), VariantAltAlleleAnnotationIndex.class);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }

        currentVariantAltAlleleAnnotationBlob = null;
      }
    }

    // FIXME support alternate alleles with 'N'
    for (byte altBase : altBases) {
      if (altBase == 'N') {
        return null;
      }
    }

    MemoryBuffer memoryBuffer = null;
    if (currentVariantAltAlleleAnnotationIndex != null) {
      int dataOffset = currentVariantAltAlleleAnnotationIndex.findDataOffset(variantAltAllele);
      if (dataOffset != -1) {
        if (currentVariantAltAlleleAnnotationBlob == null) {
          ZipArchiveEntry zipArchiveEntry =
              zipFile.getEntry(contig + "/var/" + partitionId + ".zst");
          try (FileChannel fileChannel =
              FileChannel.open(annotationsZip, StandardOpenOption.READ)) {
            ByteBuffer srcByteBuffer =
                fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    zipArchiveEntry.getDataOffset(),
                    zipArchiveEntry.getCompressedSize());
            directByteBuffer.clear();
            zstdDecompressCtx.decompressDirectByteBufferStream(directByteBuffer, srcByteBuffer);
            directByteBuffer.position(0);

            currentVariantAltAlleleAnnotationBlob =
                MemoryBuffer.fromDirectByteBuffer(
                    directByteBuffer, Math.toIntExact(zipArchiveEntry.getSize()), null);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }

        memoryBuffer = currentVariantAltAlleleAnnotationBlob.slice(dataOffset);
      }
    }

    return memoryBuffer;
  }

  @Override
  public void close() throws IOException {
    zipFile.close();
    zstdDecompressCtx.close();
    directByteBuffer = null;
  }
}
