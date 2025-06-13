package org.molgenis.vipannotate.db.v2;

public interface AnnotationDataset<T> {
  /**
   * @return annotation dataset or <code>null</code>
   */
  T findById(int index);
}
