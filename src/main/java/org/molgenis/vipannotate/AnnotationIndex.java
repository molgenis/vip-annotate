package org.molgenis.vipannotate;

public interface AnnotationIndex<T extends Feature> {
  /**
   * @return annotation data index range or <code>null</code> if no index exists for variant
   */
  IndexRange findIndexes(T feature);
}
