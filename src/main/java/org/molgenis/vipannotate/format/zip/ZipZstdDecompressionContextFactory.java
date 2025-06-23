package org.molgenis.vipannotate.format.zip;

import com.github.luben.zstd.ZstdDecompressCtx;
import org.jspecify.annotations.Nullable;

public class ZipZstdDecompressionContextFactory implements AutoCloseable {
  @Nullable private ZstdDecompressCtx zstdDecompressCtx; // one decompression context per thread

  public ZipZstdDecompressionContext create(MappableZipFile zipFile) {
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
