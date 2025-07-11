package org.molgenis.vipannotate.annotation;

public interface AnnotationIndex<T extends Feature> {
  /**
   * @return non-negative annotation data index or <code>-1</code> if no index exists for variant
   */
  int findIndex(T feature);
}
