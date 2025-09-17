package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.MemorySizeValidator.ONE_MB;

import java.io.Serial;

public class InvalidMaxHeapSizeException extends InvalidMemorySizeException {
  @Serial private static final long serialVersionUID = 1L;

  public InvalidMaxHeapSizeException(long expectedSizeBytes, long actualSizeBytes) {
    super(
        "maximum heap size (%.0f MB) is less than the required %.0f MB."
            .formatted(actualSizeBytes / (double) ONE_MB, expectedSizeBytes / (double) ONE_MB));
  }
}
