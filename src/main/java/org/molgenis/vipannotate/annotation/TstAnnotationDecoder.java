package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class TstAnnotationDecoder implements AnnotationDecoder<DoubleAnnotation> {
  private final U16ValueReader valueReader;
  private final U16ToF64ValueDecoder valueDecoder;

  @Override
  public DoubleAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    double decodedValue = getDecodedValue(memBuffer, annotationIndex);
    return new DoubleAnnotation(decodedValue);
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, DoubleAnnotation annotation) {
    double decodedValue = getDecodedValue(memBuffer, annotationIndex);
    annotation.reset(decodedValue);
  }

  private double getDecodedValue(MemoryBuffer memBuffer, int annotationIndex) {
    int encodedValue = valueReader.apply(memBuffer, annotationIndex);
    return valueDecoder.decode(encodedValue);
  }
}
