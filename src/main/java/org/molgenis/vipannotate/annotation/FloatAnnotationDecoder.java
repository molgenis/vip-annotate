package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class FloatAnnotationDecoder implements AnnotationDecoder<FloatAnnotation> {
  private final FloatReadValueFunction floatReadValueFunction;

  @Override
  public FloatAnnotation decode(BinaryReader binaryReader, int annotationIndex) {
    double value = floatReadValueFunction.apply(binaryReader, annotationIndex);
    return new FloatAnnotation(value);
  }

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, FloatAnnotation annotation) {
    double value = floatReadValueFunction.apply(binaryReader, annotationIndex);
    annotation.reset(value);
  }
}
