package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class OffsetNullableIntAnnotationDecoder
    implements AnnotationDecoder<NullableIntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;
  private final int offset;

  @Override
  public NullableIntAnnotation decode(BinaryReader binaryReader, int annotationIndex) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    if (value == 0) {
      return new NullableIntAnnotation();
    } else {
      return new NullableIntAnnotation(offset + value - 1);
    }
  }

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, NullableIntAnnotation annotation) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    if (value == 0) {
      annotation.reset();
    } else {
      annotation.reset(offset + value - 1);
    }
  }
}
