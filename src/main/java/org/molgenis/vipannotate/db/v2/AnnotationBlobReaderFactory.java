package org.molgenis.vipannotate.db.v2;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.molgenis.vipannotate.util.MappableZipFile;

public class AnnotationBlobReaderFactory implements AutoCloseable {
  private final ZipZstdDecompressionContextFactory zipZstdDecompressionContextFactory;

  public AnnotationBlobReaderFactory() {
    this.zipZstdDecompressionContextFactory = new ZipZstdDecompressionContextFactory();
  }

  public AnnotationBlobReader create(MappableZipFile zipFile, String blobId) {
    ZipZstdDecompressionContext zipZstdDecompressionContext =
        zipZstdDecompressionContextFactory.create(zipFile);

    int capacity = Math.toIntExact(calcByteBufferCapacity(zipFile, blobId));
    ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(capacity);

    return new AnnotationBlobReader(blobId, zipZstdDecompressionContext, directByteBuffer);
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
  }
}
