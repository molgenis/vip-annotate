package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.MemorySizeValidator.ONE_MB;

public class InvalidMaxHeapSizeException extends InvalidMemorySizeException {

  public InvalidMaxHeapSizeException(long expectedSizeBytes, long actualSizeBytes) {
    super(
        "maximum heap size (%d MB) is less than the required %d MB."
            .formatted(actualSizeBytes / ONE_MB, expectedSizeBytes / ONE_MB));
  }
}
