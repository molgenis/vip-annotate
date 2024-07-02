package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public record TranscriptDb(@NonNull IntervalTree intervalTree, @NonNull Transcript[] transcripts)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public List<Transcript> find(int start, int stop) {
    List<Transcript> overlappingTranscripts = new ArrayList<>();
    intervalTree.queryOverlapId(start, stop, id -> overlappingTranscripts.add(transcripts[id]));
    return overlappingTranscripts;
  }
}
