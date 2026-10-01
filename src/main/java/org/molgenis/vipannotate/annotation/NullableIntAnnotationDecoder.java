package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class NullableIntAnnotationDecoder implements AnnotationDecoder<NullableIntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public NullableIntAnnotation decode(BinaryReader binaryReader, int annotationIndex) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    return value == 0
        ? new NullableIntAnnotation()
        : new NullableIntAnnotation(value < 0 ? value : value - 1);
  }

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, NullableIntAnnotation annotation) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    if (value == 0) {
      annotation.reset();
    } else if (value < 0) {
      annotation.reset(value + 1);

    } else {
      annotation.reset(value - 1);
    }
  }
}
