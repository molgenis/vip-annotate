package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;
import static org.molgenis.vipannotate.util.Numbers.validatePositive;

/**
 * @param minStartPos minimum start position (one-based, inclusive)
 * @param maxStartPos maximum start position (one-based, inclusive)
 * @param recordCount number of records
 */
public record ContigAnalysis(long minStartPos, long maxStartPos, long recordCount) {
  public ContigAnalysis {
    validatePositive(minStartPos);
    if (maxStartPos < minStartPos) {
      throw new IllegalArgumentException();
    }
    validateNonNegative(recordCount);
  }
}
