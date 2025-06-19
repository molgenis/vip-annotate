package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegative;

import lombok.*;

/** Genomic feature annotation with an index value */
@Getter
@ToString
@EqualsAndHashCode
public class IndexedAnnotation<T extends Annotation> implements Annotation {
  /** index value */
  private final int index;

  private final T featureAnnotation;

  public IndexedAnnotation(int index, @NonNull T featureAnnotation) {
    this.index = requireNonNegative(index);
    this.featureAnnotation = featureAnnotation;
  }
}
