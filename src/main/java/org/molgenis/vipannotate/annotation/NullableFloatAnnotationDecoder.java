package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class NullableFloatAnnotationDecoder implements AnnotationDecoder<NullableFloatAnnotation> {
  private final FloatReadValueFunction readValueFunction;

  @Override
  public NullableFloatAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    double value = readValueFunction.apply(memBuffer, annotationIndex);
    return Double.isNaN(value) ? new NullableFloatAnnotation() : new NullableFloatAnnotation(value);
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, NullableFloatAnnotation annotation) {
    double value = readValueFunction.apply(memBuffer, annotationIndex);
    if (Double.isNaN(value)) {
      annotation.reset();
    } else {
      annotation.reset(value);
    }
  }
}
