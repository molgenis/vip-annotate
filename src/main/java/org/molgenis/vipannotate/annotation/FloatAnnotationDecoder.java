package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class FloatAnnotationDecoder implements AnnotationDecoder<FloatAnnotation> {
  private final FloatReadValueFunction readValueFunction;

  @Override
  public void decodeInto(
      MemoryDecoder memDecoder, int annotationIndex, FloatAnnotation annotation) {
    double value = readValueFunction.apply(memDecoder, annotationIndex);
    annotation.reset(value);
  }
}
