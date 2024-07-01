package org.molgenis.vipannotate.format.zip;

import org.jspecify.annotations.Nullable;
import org.molgenis.zstd.Zstd;
import org.molgenis.zstd.ZstdDecompressionContext;

public class ZipZstdDecompressionContextFactory implements AutoCloseable {
  private final Zstd zstd;

  @Nullable
  private ZstdDecompressionContext zstdDecompressionContext; // one decompression context per thread

  public ZipZstdDecompressionContextFactory() {
    this.zstd = Zstd.create();
  }

  public ZipZstdDecompressionContext create(MappableZipFile zipFile) {
    if (zstdDecompressionContext == null) {
      zstdDecompressionContext = zstd.createDecompressionContext();
    }
    return new ZipZstdDecompressionContext(zipFile, zstdDecompressionContext);
  }

  @Override
  public void close() {
    if (zstdDecompressionContext != null) {
      zstdDecompressionContext.close();
    }
    zstd.close();
  }
}
