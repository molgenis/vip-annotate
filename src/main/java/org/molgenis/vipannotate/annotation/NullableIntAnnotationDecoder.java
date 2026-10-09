package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class NullableIntAnnotationDecoder implements AnnotationDecoder<NullableIntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, NullableIntAnnotation annotation) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    if (value == 0) {
      annotation.reset();
    } else if (value < 0) {
      annotation.reset(value);
    } else {
      annotation.reset(value - 1);
    }
  }
}
