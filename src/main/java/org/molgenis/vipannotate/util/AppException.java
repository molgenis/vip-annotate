package org.molgenis.vipannotate.util;

import lombok.Getter;

/** root application exception */
@Getter
public class AppException extends RuntimeException {
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
