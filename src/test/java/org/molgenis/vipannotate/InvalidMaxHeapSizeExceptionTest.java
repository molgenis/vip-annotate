package org.molgenis.vipannotate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.util.ErrorCode;

class InvalidMaxHeapSizeExceptionTest {
  @Test
  void getMessageAndGetErrorCode() {
    InvalidMaxHeapSizeException invalidMaxHeapSizeException =
        new InvalidMaxHeapSizeException(16 * 1048576L, 8 * 1048576L);
    assertAll(
        () ->
            Assertions.assertEquals(
                ErrorCode.VALIDATION_ERROR, invalidMaxHeapSizeException.getErrorCode()),
        () ->
            assertEquals(
                "maximum heap size (8 MB) is less than the required 16 MB. suggestion: run the application with -Xms460m -Xmx460m -XX:MaxDirectMemorySize=512m",
                invalidMaxHeapSizeException.getMessage()));
  }
}
