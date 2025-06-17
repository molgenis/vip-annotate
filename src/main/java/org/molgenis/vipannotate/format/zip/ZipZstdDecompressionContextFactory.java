package org.molgenis.vipannotate.format.zip;

import com.github.luben.zstd.ZstdDecompressCtx;
import lombok.NonNull;

public class ZipZstdDecompressionContextFactory implements AutoCloseable {
  private ZstdDecompressCtx zstdDecompressCtx; // one decompression context per thread

  public ZipZstdDecompressionContext create(@NonNull MappableZipFile zipFile) {
    if (zstdDecompressCtx == null) {
      zstdDecompressCtx = new ZstdDecompressCtx();
    }
    return new ZipZstdDecompressionContext(zipFile, zstdDecompressCtx);
  }

  @Override
  public void close() {
    if (zstdDecompressCtx != null) {
      zstdDecompressCtx.close();
    }
  }
}
