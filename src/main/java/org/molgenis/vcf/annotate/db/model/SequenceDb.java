package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

@Builder
public record SequenceDb(@NonNull IntervalTree intervalTree, @Singular List<Sequence> sequences)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public Sequence find(int start, int stop) {
    List<IntervalTree.QueryResult> queryResults = intervalTree.queryOverlap(start, stop);
    List<Sequence> sequenceList =
        queryResults.stream().map(queryResult -> sequences.get(queryResult.id)).toList();
    if (sequenceList.size() > 1) throw new RuntimeException();
    return sequenceList.getFirst();
  }
}
