package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

final class EmptyAnnotationIndex<T extends Feature> implements AnnotationIndex<T> {
  private static final EmptyAnnotationIndex<?> INSTANCE = new EmptyAnnotationIndex<>();

  private EmptyAnnotationIndex() {}

  @SuppressWarnings("unchecked")
  public static <T extends Feature> EmptyAnnotationIndex<T> getInstance() {
    return (EmptyAnnotationIndex<T>) INSTANCE;
  }

  @Override
  public @Nullable IndexRange findIndexes(T feature) {
    return null;
  }
}
