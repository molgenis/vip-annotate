package org.molgenis.vipannotate.util;

import lombok.Getter;

@Getter
public enum ErrorCode {
  SUCCESS(0),
  VALIDATION_ERROR(2);

  private final int code;

  ErrorCode(int code) {
    this.code = code;
  }
}
