package org.molgenis.vipannotate.util;

import java.io.Serial;
import lombok.Getter;

/** root application exception */
@Getter
public class AppException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final ErrorCode errorCode;

  public AppException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  @Override
  public String getMessage() {
    String message = super.getMessage();
    return message != null ? message : "error code %d".formatted(getErrorCode().getCode());
  }
}
