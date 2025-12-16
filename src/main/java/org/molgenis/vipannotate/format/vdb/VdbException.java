package org.molgenis.vipannotate.format.vdb;

import java.io.Serial;

public class VdbException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public VdbException(String message) {
    super(message);
  }

  public VdbException(Throwable cause) {
    super(cause);
  }
}
