package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class IntAnnotationDecoder implements AnnotationDecoder<IntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public void decodeInto(BinaryReader binaryReader, int annotationIndex, IntAnnotation annotation) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    annotation.setValue(value);
  }
}
