package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationDecoder implements AnnotationDecoder<FloatAnnotation> {
  private final Quantizer quantizer;
  private final IntReadValueFunction intReadValueFunction;

  @Override
  public void decodeInto(
      MemoryDecoder memDecoder, int annotationIndex, FloatAnnotation annotation) {
    long quantizedValue = intReadValueFunction.apply(memDecoder, annotationIndex);
    double value = quantizer.dequantize(quantizedValue);
    annotation.reset(value);
  }
}
