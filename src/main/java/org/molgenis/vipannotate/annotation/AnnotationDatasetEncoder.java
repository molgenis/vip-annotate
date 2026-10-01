package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.SizedIterator;

/// encodes annotations
public interface AnnotationDatasetEncoder<T extends Annotation> {
  /// encodes annotations into provided memory encoders
  ///
  /// @param annotationIt iterator over the annotations to encode
  /// @param encoderProvider provides memory encoders for specific dataset components
  void encodeInto(SizedIterator<T> annotationIt, MemoryEncoderProvider encoderProvider);
}
