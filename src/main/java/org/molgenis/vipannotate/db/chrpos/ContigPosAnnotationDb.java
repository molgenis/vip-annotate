package org.molgenis.vipannotate.db.chrpos;

import static java.util.Objects.requireNonNull;

import com.github.luben.zstd.*;
import java.io.*;
import java.nio.ByteBuffer;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.format.AnnotationDb;
import org.molgenis.vipannotate.db.exact.format.AnnotationDecoder;
import org.molgenis.vipannotate.util.MappableZipFile;

// TODO replace Double with container that will contain min/max/mean/median for non-SNPs later
public class ContigPosAnnotationDb implements AnnotationDb<Double> {
  public static final String ID = "pos";
  public static final int NR_PARTITION_ID_BITS = 20;

  private final MappableZipFile zipFile;
  private final AnnotationDecoder<Double> annotationDecoder;
  private final int nrAnnotationBytes;
  private final ZstdDecompressCtx zstdDecompressCtxData;

  private ByteBuffer directByteBufferData;

  private MemoryBuffer currentAnnotationBlob;
  private String currentContig;
  private int currentPartitionId = -1;

  public ContigPosAnnotationDb(
      MappableZipFile zipFile, AnnotationDecoder<Double> annotationDecoder, int nrAnnotationBytes) {
    this.zipFile = requireNonNull(zipFile);
    this.annotationDecoder = requireNonNull(annotationDecoder);
    this.nrAnnotationBytes = nrAnnotationBytes;

    this.zstdDecompressCtxData = new ZstdDecompressCtx();
    int bufferCapacity = ((int) Math.pow(2, NR_PARTITION_ID_BITS)) * nrAnnotationBytes;
    this.directByteBufferData = ByteBuffer.allocateDirect(bufferCapacity);
  }

  @Override
  public Double findAnnotations(Variant variant) {
    int refLength = variant.getRefLength();
    int altLength = variant.getAltLength();
    if (refLength != 1 || altLength != 1) {
      return null; // TODO annotate non-SNPs
    }

    String contig = variant.contig();
    int partitionId = variant.start() >> NR_PARTITION_ID_BITS;
    boolean partitionIdChanged = partitionId != currentPartitionId;
    boolean contigChanged = !contig.equals(currentContig);

    if (partitionIdChanged || contigChanged) {
      currentAnnotationBlob = loadAnnotationData(contig, partitionId);
      if (currentAnnotationBlob == null) {
        return null;
      }

      currentPartitionId = partitionId;
      currentContig = contig;
    }

    int partitionPos = variant.start() - (partitionId << NR_PARTITION_ID_BITS);
    currentAnnotationBlob.readerIndex(partitionPos * nrAnnotationBytes);
    return annotationDecoder.decode(currentAnnotationBlob);
  }

  @Override
  public void close() {
    directByteBufferData = null; // make available for deallocation
    zstdDecompressCtxData.close();
  }

  private MemoryBuffer loadAnnotationData(String contig, int partitionId) {
    ZipArchiveEntry zipArchiveEntry =
        zipFile.getEntry(contig + "/" + partitionId + "/" + "scores.zst");
    if (zipArchiveEntry == null) {
      return null;
    }

    int compressedSize = Math.toIntExact(zipArchiveEntry.getCompressedSize());
    int uncompressedSize = Math.toIntExact(zipArchiveEntry.getSize());

    directByteBufferData.clear();
    ByteBuffer srcByteBuffer = this.zipFile.map(zipArchiveEntry);
    zstdDecompressCtxData.decompressDirectByteBuffer(
        directByteBufferData, 0, uncompressedSize, srcByteBuffer, 0, compressedSize);
    //noinspection UnusedAssignment
    srcByteBuffer = null; // mark for garbage collection
    directByteBufferData.position(0);

    return MemoryBuffer.fromDirectByteBuffer(directByteBufferData, uncompressedSize, null);
  }
}
