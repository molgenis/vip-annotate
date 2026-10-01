package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;

@RequiredArgsConstructor
public class OffsetIntAnnotationDecoder implements AnnotationDecoder<IntAnnotation> {
  private final IntReadValueFunction intReadValueFunction;
  private final int offset;

  @Override
  public IntAnnotation decode(BinaryReader binaryReader, int annotationIndex) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    return new IntAnnotation(offset + value);
  }

  @Override
  public void decodeInto(BinaryReader binaryReader, int annotationIndex, IntAnnotation annotation) {
    long value = intReadValueFunction.apply(binaryReader, annotationIndex);
    annotation.setValue(offset + value);
  }
}
