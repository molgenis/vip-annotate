package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class OffsetNullableIntAnnotationDecoder
    implements AnnotationDecoder<NullableIntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;
  private final int offset;

  @Override
  public NullableIntAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    int value = intReadValueFunction.apply(memBuffer, annotationIndex);
    if (value == 0) {
      return new NullableIntAnnotation();
    } else {
      return new NullableIntAnnotation(offset + value - 1);
    }
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, NullableIntAnnotation annotation) {
    int value = intReadValueFunction.apply(memBuffer, annotationIndex);
    if (value == 0) {
      annotation.reset();
    } else {
      annotation.reset(offset + value - 1);
    }
  }
}
