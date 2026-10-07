package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegative;

/**
 * @param minStartPos minimum start position (one-based, inclusive)
 * @param maxStartPos maximum start position (one-based, inclusive)
 * @param recordCount number of records
 */
public record ContigAnalysis(long minStartPos, long maxStartPos, long recordCount) {
  public ContigAnalysis {
    requireNonNegative(minStartPos);
    if (maxStartPos < minStartPos) {
      throw new IllegalArgumentException();
    }
    requireNonNegative(recordCount);
  }
}
