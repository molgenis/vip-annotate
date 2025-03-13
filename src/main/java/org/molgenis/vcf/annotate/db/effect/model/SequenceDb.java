package org.molgenis.vcf.annotate.db.effect.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.NonNull;

public record SequenceDb(@NonNull IntervalTree intervalTree, @NonNull Sequence[] sequences)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * @return first sequence overlapping with [start, stop] or <code>null</code>
   */
  public Sequence findAnySequence(int start, int stop) {
    // stop + 1, because interval tree builder requires [x, y) interval
    int sequenceId = intervalTree.queryAnyOverlapId(start, stop + 1);
    return sequenceId != -1 ? sequences[sequenceId] : null;
  }
}
