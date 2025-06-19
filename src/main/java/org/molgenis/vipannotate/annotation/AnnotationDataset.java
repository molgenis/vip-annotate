package org.molgenis.vipannotate.annotation;

public interface AnnotationDataset<T extends Annotation> {
  /**
   * @return annotation dataset or <code>null</code>
   */
  T findByIndex(int index);
}
