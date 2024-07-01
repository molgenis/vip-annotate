package org.molgenis.vipannotate.annotation;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.molgenis.vipannotate.format.zip.MappableZipFile;
import org.molgenis.vipannotate.format.zip.ZipZstdDecompressionContext;
import org.molgenis.vipannotate.format.zip.ZipZstdDecompressionContextFactory;

public class AnnotationBlobReaderFactory implements AutoCloseable {
  private final Arena arena;
  private final ZipZstdDecompressionContextFactory zipZstdDecompressionContextFactory;

  public AnnotationBlobReaderFactory() {
    //noinspection DataFlowIssue
    this.arena = Arena.ofConfined();
    this.zipZstdDecompressionContextFactory = new ZipZstdDecompressionContextFactory();
  }

  public AnnotationBlobReader create(MappableZipFile zipFile, String blobId) {
    ZipZstdDecompressionContext zipZstdDecompressionContext =
        zipZstdDecompressionContextFactory.create(zipFile);

    int capacity = Math.toIntExact(calcByteBufferCapacity(zipFile, blobId));
    MemorySegment memorySegment = arena.allocate(capacity);

    //noinspection DataFlowIssue
    return new AnnotationBlobReader(
        blobId, new ZipZstdPartitionDatasetReader(zipZstdDecompressionContext), memorySegment);
  }

  private static long calcByteBufferCapacity(MappableZipFile zipFile, String blobId) {
    String entrySuffix = "/" + blobId + ".zst";

    long maxSize = 0;
    for (Iterator<ZipArchiveEntry> it = zipFile.getEntries(); it.hasNext(); ) {
      ZipArchiveEntry entry = it.next();
      if (entry.getName().endsWith(entrySuffix)) {
        long size = entry.getSize();
        if (size > maxSize) {
          maxSize = size;
        }
      }
    }

    return maxSize;
  }

  @Override
  public void close() {
    zipZstdDecompressionContextFactory.close();
    arena.close();
  }
}
