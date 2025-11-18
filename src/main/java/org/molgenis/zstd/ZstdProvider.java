package org.molgenis.zstd;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;

public enum ZstdProvider implements AutoCloseable {
  INSTANCE;

  @Nullable private static Zstd zstd;

  public Zstd get() {
    if (zstd == null) {
      zstd = Zstd.create();
    }
    return zstd;
  }

  @Override
  public void close() {
    ClosableUtils.close(zstd);
    zstd = null;
  }
}
