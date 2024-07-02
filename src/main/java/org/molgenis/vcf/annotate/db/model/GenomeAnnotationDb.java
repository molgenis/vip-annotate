package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record GenomeAnnotationDb(@NonNull EnumMap<Chromosome, AnnotationDb> annotationDbs)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public AnnotationDb get(Chromosome chromosome) {
    return annotationDbs.get(chromosome);
  }

  public List<Transcript> findTranscripts(Chromosome chromosome, int start, int stop) {
    AnnotationDb annotationDb = annotationDbs().get(chromosome);
    return annotationDb.findTranscripts(start, stop);
  }

  public byte[] findSequence(Chromosome chromosome, int start, int stop) {
    AnnotationDb annotationDb = annotationDbs().get(chromosome);
    return annotationDb.findSequence(start, stop);
  }
}
