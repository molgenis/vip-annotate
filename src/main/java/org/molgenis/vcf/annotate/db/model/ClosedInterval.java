package org.molgenis.vcf.annotate.db.model;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public class ClosedInterval {
  /** start in closed interval [start, stop] */
  int start;

  /** stop - start */
  int length;

  /** stop in closed interval [start, stop] */
  public int getStop() {
    return start + length - 1;
  }

  /**
   * @return true if [start, stop] overlaps with [x, y]
   */
  public boolean isOverlapping(long start, long stop) {
    int intervalStart = getStart();
    int intervalStop = getStop();
    return ((start >= intervalStart && start <= intervalStop)
        || (stop >= intervalStart && stop <= intervalStop)
        || (start < intervalStart && stop > intervalStop));
  }
}
