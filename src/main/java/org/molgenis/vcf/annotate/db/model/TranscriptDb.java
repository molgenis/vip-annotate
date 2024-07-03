package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public record TranscriptDb(@NonNull IntervalTree intervalTree, @NonNull Transcript[] transcripts)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * @return overlapping [start, stop) transcripts
   */
  public List<Transcript> findOverlap(int start, int stop) {
    List<Transcript> overlappingTranscripts = new ArrayList<>();
    // end + 1, because interval tree builder requires [x, y) interval
    intervalTree.queryOverlapId(start, stop + 1, id -> overlappingTranscripts.add(transcripts[id]));
    return overlappingTranscripts;
  }
}
