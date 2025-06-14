package org.molgenis.vipannotate.annotation;

public interface AnnotationDataset<T> {
  /**
   * @return annotation dataset or <code>null</code>
   */
  T findByIndex(int index);
}
