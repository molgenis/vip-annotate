package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public interface AnnotationDataset<T extends Annotation> {
  /**
   * @return annotation dataset or <code>null</code>
   */
  @Nullable T findByIndex(int index);
}
