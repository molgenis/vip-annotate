package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public interface AnnotationDataset<T extends @Nullable Annotation> {
  /**
   * @return annotation dataset or <code>null</code>
   */
  T findByIndex(int index);
}
