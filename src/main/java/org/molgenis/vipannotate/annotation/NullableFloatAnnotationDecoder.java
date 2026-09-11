package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableDoubleAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class NullableFloatAnnotationDecoder implements AnnotationDecoder<NullableDoubleAnnotation> {
  private final FloatReadValueFunction readValueFunction;

  @Override
  public NullableDoubleAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    double value = readValueFunction.apply(memBuffer, annotationIndex);
    return Double.isNaN(value)
        ? new NullableDoubleAnnotation()
        : new NullableDoubleAnnotation(value);
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, NullableDoubleAnnotation annotation) {
    double value = readValueFunction.apply(memBuffer, annotationIndex);
    if (Double.isNaN(value)) {
      annotation.reset();
    } else {
      annotation.reset(value);
    }
  }
}
