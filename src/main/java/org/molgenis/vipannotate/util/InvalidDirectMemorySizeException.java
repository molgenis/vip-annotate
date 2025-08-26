package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.MemorySizeValidator.ONE_MB;

public class InvalidDirectMemorySizeException extends InvalidMemorySizeException {
  public InvalidDirectMemorySizeException(long expectedSizeBytes, long actualSizeBytes) {
    super(
        "maximum direct memory size (%d MB) is less than the required %d MB"
            .formatted(actualSizeBytes / ONE_MB, expectedSizeBytes / ONE_MB));
  }
}
