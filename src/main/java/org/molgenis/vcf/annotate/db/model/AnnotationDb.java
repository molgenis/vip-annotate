package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.NonNull;

public record AnnotationDb(
    @NonNull TranscriptDb transcriptDb, @NonNull Gene[] genes, @NonNull SequenceDb sequenceDb)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public List<Transcript> findOverlapTranscripts(int start, int stop) {
    return transcriptDb.findOverlap(start, stop);
  }

  public Gene getGene(Transcript transcript) {
    return genes[transcript.getGeneIndex()];
  }

  /**
   * @return sequence in [start, stop] if start <= stop, or the reverse sequence in [stop, start]
   */
  public char[] getSequence(int start, int stop, Strand strand) {
    Sequence sequence =
        switch (strand) {
          case POSITIVE -> sequenceDb.findAnySequence(start, stop);
          case NEGATIVE -> sequenceDb.findAnySequence(stop, start);
        };
    return sequence != null ? sequence.get(start, stop, strand) : null;
  }
}
