package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class NullableIntAnnotationDecoder implements AnnotationDecoder<NullableIntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public NullableIntAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    int value = intReadValueFunction.apply(memBuffer, annotationIndex);
    return value == 0
        ? new NullableIntAnnotation()
        : new NullableIntAnnotation(value < 0 ? value : value - 1);
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, NullableIntAnnotation annotation) {
    int value = intReadValueFunction.apply(memBuffer, annotationIndex);
    if (value == 0) {
      annotation.reset();
    } else if (value < 0) {
      annotation.reset(value + 1);

    } else {
      annotation.reset(value - 1);
    }
  }
}
