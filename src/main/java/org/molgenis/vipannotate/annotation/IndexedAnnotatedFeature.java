package org.molgenis.vipannotate.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Annotated genomic feature with an index value */
@Getter
@ToString
@EqualsAndHashCode
public class IndexedAnnotatedFeature<
        T extends Feature, U extends Annotation, V extends AnnotatedFeature<T, U>>
    implements AnnotatedFeature<T, U> {
  /** index value */
  private final int index;

  /** genomic interval annotation */
  private final V annotatedFeature;

  public IndexedAnnotatedFeature(int index, @NonNull V annotatedFeature) {
    this.index = index;
    this.annotatedFeature = annotatedFeature;
  }

  @Override
  public T getFeature() {
    return annotatedFeature.getFeature();
  }

  @Override
  public U getAnnotation() {
    return annotatedFeature.getAnnotation();
  }
}
