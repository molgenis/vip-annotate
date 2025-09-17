package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AppExceptionTest {
  @Test
  void getMessageAndErrorCode() {
    AppException appException = new AppException(ErrorCode.VALIDATION_ERROR, "msg");
    assertAll(
        () -> assertEquals(ErrorCode.VALIDATION_ERROR, appException.getErrorCode()),
        () -> assertEquals("msg", appException.getMessage()));
  }

  @Test
  void getMessageNull() {
    AppException appException = new AppException(ErrorCode.VALIDATION_ERROR);
    assertAll(
        () -> assertEquals(ErrorCode.VALIDATION_ERROR, appException.getErrorCode()),
        () -> assertEquals("error code 2", appException.getMessage()));
  }
}
