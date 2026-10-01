package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationEncoder implements AnnotationEncoder<FloatAnnotation> {
  private final Quantizer quantizer;
  private final IntValueWriter intValueWriter;

  @Override
  public void encodeInto(FloatAnnotation annotation, MemoryEncoder memEncoder) {
    if (annotation.isMissingValue()) {
      throw new IllegalStateException();
    }
    long quantizedValue = quantizer.quantize(annotation.getValue());
    intValueWriter.write(quantizedValue, memEncoder);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
