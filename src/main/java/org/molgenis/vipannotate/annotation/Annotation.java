package org.molgenis.vipannotate.annotation;

/// genomic feature annotation
public sealed interface Annotation
    permits CompositeAnnotation, ScalarAnnotation, StringAnnotation, StringListAnnotation {
  boolean isMissingValue();

  /// resets annotation such that it represents a missing value
  void reset();
}
