package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class FloatAnnotationDecoder implements AnnotationDecoder<DoubleAnnotation> {
  private final FloatReadValueFunction floatReadValueFunction;

  @Override
  public DoubleAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    double value = floatReadValueFunction.apply(memBuffer, annotationIndex);
    return new DoubleAnnotation(value);
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, DoubleAnnotation annotation) {
    double value = floatReadValueFunction.apply(memBuffer, annotationIndex);
    annotation.reset(value);
  }
}
