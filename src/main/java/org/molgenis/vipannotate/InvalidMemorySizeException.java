package org.molgenis.vipannotate;

import static org.molgenis.vipannotate.MemorySizeValidator.ONE_MB;
import static org.molgenis.vipannotate.MemorySizeValidator.THRESHOLD_HEAP_SIZE;

import java.io.Serial;
import org.molgenis.vipannotate.util.ErrorCode;

public abstract class InvalidMemorySizeException extends AppException {
  @Serial private static final long serialVersionUID = 1L;

  public InvalidMemorySizeException(String message) {
    super(
        ErrorCode.VALIDATION_ERROR,
        "%s suggestion: run the application with -Xms%dm -Xmx%dm -XX:MaxDirectMemorySize=%dm"
            .formatted(
                message,
                THRESHOLD_HEAP_SIZE / ONE_MB,
                THRESHOLD_HEAP_SIZE / ONE_MB,
                MemorySizeValidator.THRESHOLD_DIRECT_MEMORY / ONE_MB));
  }
}
