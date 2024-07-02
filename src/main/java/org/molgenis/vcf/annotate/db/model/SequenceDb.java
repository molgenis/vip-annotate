package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public record SequenceDb(@NonNull IntervalTree intervalTree, @NonNull Sequence[] sequences)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public Sequence find(int start, int stop) {
    List<Sequence> overlappingSequences = new ArrayList<>(1);
    intervalTree.queryOverlapId(start, stop, id -> overlappingSequences.add(sequences[id]));
    if (overlappingSequences.size() > 1) throw new RuntimeException();
    return !overlappingSequences.isEmpty() ? overlappingSequences.getFirst() : null;
  }
}
