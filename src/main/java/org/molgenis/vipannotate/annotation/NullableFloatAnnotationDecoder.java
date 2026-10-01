package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class NullableFloatAnnotationDecoder implements AnnotationDecoder<NullableFloatAnnotation> {
  private final FloatReadValueFunction readValueFunction;

  @Override
  public NullableFloatAnnotation decode(BinaryReader binaryReader, int annotationIndex) {
    double value = readValueFunction.apply(binaryReader, annotationIndex);
    return Double.isNaN(value) ? new NullableFloatAnnotation() : new NullableFloatAnnotation(value);
  }

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, NullableFloatAnnotation annotation) {
    double value = readValueFunction.apply(binaryReader, annotationIndex);
    if (Double.isNaN(value)) {
      annotation.reset();
    } else {
      annotation.reset(value);
    }
  }
}
