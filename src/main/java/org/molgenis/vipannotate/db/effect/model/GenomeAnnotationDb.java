package org.molgenis.vipannotate.db.effect.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record GenomeAnnotationDb(
    @NonNull EnumMap<FuryFactory.Chromosome, FuryFactory.AnnotationDb> annotationDbs)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public FuryFactory.AnnotationDb get(FuryFactory.Chromosome chromosome) {
    return annotationDbs.get(chromosome);
  }
}
