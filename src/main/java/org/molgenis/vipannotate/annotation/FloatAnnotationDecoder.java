package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class FloatAnnotationDecoder implements AnnotationDecoder<FloatAnnotation> {
  private final FloatReadValueFunction readValueFunction;

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, FloatAnnotation annotation) {
    double value = readValueFunction.apply(binaryReader, annotationIndex);
    annotation.reset(value);
  }
}
