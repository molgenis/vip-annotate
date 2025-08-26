package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.MemorySizeValidator.ONE_MB;
import static org.molgenis.vipannotate.util.MemorySizeValidator.THRESHOLD_HEAP_SIZE;

public abstract class InvalidMemorySizeException extends AppException {

  public InvalidMemorySizeException(String message) {
    super(
        "%s suggestion: run the application with -Xms%dm -Xmx%dm -XX:MaxDirectMemorySize=%dm"
            .formatted(
                message,
                THRESHOLD_HEAP_SIZE / ONE_MB,
                THRESHOLD_HEAP_SIZE / ONE_MB,
                MemorySizeValidator.THRESHOLD_DIRECT_MEMORY / ONE_MB),
        ErrorCode.VALIDATION_ERROR);
  }
}
