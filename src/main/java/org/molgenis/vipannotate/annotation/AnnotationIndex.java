package org.molgenis.vipannotate.annotation;

public interface AnnotationIndex {
  /**
   * @return non-negative annotation data index or <code>-1</code> if no index exists for variant
   */
  int findIndex(SequenceVariant variant);
}
