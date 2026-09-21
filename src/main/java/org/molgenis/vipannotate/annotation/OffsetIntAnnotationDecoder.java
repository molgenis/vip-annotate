package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class OffsetIntAnnotationDecoder implements AnnotationDecoder<IntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;
  private final int offset;

  @Override
  public IntAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    long value = intReadValueFunction.apply(memBuffer, annotationIndex);
    return new IntAnnotation(offset + value);
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, IntAnnotation annotation) {
    long value = intReadValueFunction.apply(memBuffer, annotationIndex);
    annotation.setValue(offset + value);
  }
}
