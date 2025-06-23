package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

/**
 * Annotated genomic feature
 *
 * @param <T> type of genomic feature
 * @param <U> type of annotation
 */
public interface AnnotatedFeature<T extends Feature, U extends Annotation> {
  /** returns genomic feature */
  T getFeature();

  /** returns annotation for genomic feature */
  @Nullable U getAnnotation();
}
