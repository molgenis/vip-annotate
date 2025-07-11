package org.molgenis.vipannotate.annotation;

final class EmptyAnnotationIndex<T extends Feature> implements AnnotationIndex<T> {
  private static final EmptyAnnotationIndex<?> INSTANCE = new EmptyAnnotationIndex<>();

  private EmptyAnnotationIndex() {}

  @SuppressWarnings("unchecked")
  public static <T extends Feature> EmptyAnnotationIndex<T> getInstance() {
    return (EmptyAnnotationIndex<T>) INSTANCE;
  }

  @Override
  public int findIndex(T feature) {
    return -1;
  }
}
