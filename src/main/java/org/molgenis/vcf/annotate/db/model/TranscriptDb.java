package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

@Builder
public record TranscriptDb(
    @NonNull IntervalTree intervalTree, @Singular List<Transcript> transcripts)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public List<Transcript> find(int start, int stop) {
    List<IntervalTree.QueryResult> queryResults = intervalTree.queryOverlap(start, stop);
    return queryResults.stream().map(queryResult -> transcripts.get(queryResult.id)).toList();
  }
}
