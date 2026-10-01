package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;

@RequiredArgsConstructor
public class IntAnnotationDecoder implements AnnotationDecoder<IntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public void decodeInto(MemoryDecoder memDecoder, int annotationIndex, IntAnnotation annotation) {
    long value = intReadValueFunction.apply(memDecoder, annotationIndex);
    annotation.reset(value);
  }
}
