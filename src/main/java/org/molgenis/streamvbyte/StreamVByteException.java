package org.molgenis.streamvbyte;

import java.io.Serial;

public class StreamVByteException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public StreamVByteException(Throwable cause) {
    super(cause);
  }
}
