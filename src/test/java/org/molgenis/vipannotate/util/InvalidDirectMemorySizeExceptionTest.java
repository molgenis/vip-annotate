package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidDirectMemorySizeExceptionTest {
  @Test
  void getMessageAndGetErrorCode() {
    InvalidDirectMemorySizeException invalidDirectMemorySizeException =
        new InvalidDirectMemorySizeException(16 * 1048576L, 8 * 1048576L);
    assertAll(
        () ->
            assertEquals(
                ErrorCode.VALIDATION_ERROR, invalidDirectMemorySizeException.getErrorCode()),
        () ->
            assertEquals(
                "maximum direct memory size (8 MB) is less than the required 16 MB. suggestion: run the application with -Xms230m -Xmx230m -XX:MaxDirectMemorySize=512m",
                invalidDirectMemorySizeException.getMessage()));
  }
}
