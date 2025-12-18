package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class IntAnnotationDecoder implements AnnotationDecoder<IntAnnotation> {
  private final ReadValueFunction readValueFunction;

  @Override
  public IntAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    int value = readValueFunction.apply(memBuffer, annotationIndex);
    return new IntAnnotation(value);
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, IntAnnotation annotation) {
    int value = readValueFunction.apply(memBuffer, annotationIndex);
    annotation.setValue(value);
  }
}
