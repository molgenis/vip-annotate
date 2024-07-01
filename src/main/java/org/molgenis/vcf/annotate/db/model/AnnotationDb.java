package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record AnnotationDb(@NonNull TranscriptDb transcriptDb, SequenceDb sequenceDb)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public List<Transcript> findTranscripts(int start, int stop) {
    return transcriptDb.find(start, stop);
  }

  public byte[] findSequence(int start, int stop) {
    Sequence sequence = sequenceDb.find(start, stop);
    if (sequence == null) return null;
    return sequence.get(start, stop);
  }
}
