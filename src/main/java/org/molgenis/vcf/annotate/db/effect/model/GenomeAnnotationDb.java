package org.molgenis.vcf.annotate.db.effect.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record GenomeAnnotationDb(@NonNull EnumMap<Chromosome, AnnotationDb> annotationDbs)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public AnnotationDb get(Chromosome chromosome) {
    return annotationDbs.get(chromosome);
  }
}
