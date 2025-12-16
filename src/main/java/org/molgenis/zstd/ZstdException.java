package org.molgenis.zstd;

import java.io.Serial;

public class ZstdException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public ZstdException(String message) {
    super(message);
  }

  public ZstdException(Throwable cause) {
    super(cause);
  }
}
