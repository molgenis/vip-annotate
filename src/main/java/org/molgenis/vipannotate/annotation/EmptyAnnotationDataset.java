package org.molgenis.vipannotate.annotation;

public final class EmptyAnnotationDataset<T> implements AnnotationDataset<T> {
  private static final EmptyAnnotationDataset<?> INSTANCE = new EmptyAnnotationDataset<>();

  private EmptyAnnotationDataset() {}

  @SuppressWarnings("unchecked")
  public static <T> EmptyAnnotationDataset<T> getInstance() {
    return (EmptyAnnotationDataset<T>) INSTANCE;
  }

  @Override
  public T findById(int index) {
    return null;
  }
}
