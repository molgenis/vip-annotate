package org.molgenis.vipannotate.annotation;

public interface LocusAnnotation<T> {
  /**
   * @return reference genome contig identifier
   */
  String contig();

  /**
   * @return reference genome start position (inclusive, 1-based)
   */
  int start();

  /**
   * @return reference genome stop position (inclusive, 1-based)
   */
  int stop();

  /**
   * @return annotation data
   */
  T annotation();
}
