package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class FloatAnnotationDecoder implements AnnotationDecoder<FloatAnnotation> {
  private final FloatReadValueFunction floatReadValueFunction;

  @Override
  public FloatAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    double value = floatReadValueFunction.apply(memBuffer, annotationIndex);
    return new FloatAnnotation(value);
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, FloatAnnotation annotation) {
    double value = floatReadValueFunction.apply(memBuffer, annotationIndex);
    annotation.reset(value);
  }
}
