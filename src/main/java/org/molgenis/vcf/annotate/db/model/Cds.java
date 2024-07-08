package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.SuperBuilder;

/**
 * A contiguous sequence which begins with, and includes, a start codon and ends with, and includes,
 * a stop codon.
 *
 * @see <a
 *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0000316">SO:0000316</a>
 */
@Builder
public record Cds(@NonNull String proteinId, @NonNull Fragment[] fragments)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * @return first cds fragment overlapping with [start, stop] or <code>null</code>
   */
  public Fragment findAnyFragment(long start, long stop) {
    for (Fragment fragment : fragments) {
      if (fragment.isOverlapping(start, stop)) return fragment;
    }
    return null;
  }

  /**
   * A CDS fragment.
   *
   * @see <a
   *     href="http://www.sequenceontology.org/browser/current_release/term/SO:0001384">SO:0001384</a>
   */
  @Value
  @EqualsAndHashCode(callSuper = true)
  @SuperBuilder
  public static class Fragment extends ClosedInterval implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    /** Indicates where the next codon begins relative to the 5' end of the current CDS feature. */
    byte phase;
  }
}
