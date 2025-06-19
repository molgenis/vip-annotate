package org.molgenis.vipannotate.annotation;

public final class EmptyAnnotationDataset<T extends Annotation> implements AnnotationDataset<T> {
  private static final EmptyAnnotationDataset<?> INSTANCE = new EmptyAnnotationDataset<>();

  private EmptyAnnotationDataset() {}

  @SuppressWarnings("unchecked")
  public static <T extends Annotation> EmptyAnnotationDataset<T> getInstance() {
    return (EmptyAnnotationDataset<T>) INSTANCE;
  }

  @Override
  public T findByIndex(int index) {
    return null;
  }
}
