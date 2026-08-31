package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableDoubleAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationDecoder implements AnnotationDecoder<ScalarAnnotation> {
  private final Quantizer quantizer;
  private final ReadValueFunction readValueFunction;
  @Nullable private final Integer nullValue;

  @Override
  public ScalarAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    int quantizedValue = readValueFunction.apply(memBuffer, annotationIndex);

    ScalarAnnotation scalarAnnotation;
    if (nullValue != null && quantizedValue == nullValue) {
      scalarAnnotation = new NullableDoubleAnnotation();
    } else {
      double value = quantizer.dequantize(quantizedValue);
      scalarAnnotation =
          nullValue != null ? new NullableDoubleAnnotation(value) : new DoubleAnnotation(value);
    }
    return scalarAnnotation;
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, ScalarAnnotation annotation) {
    throw new RuntimeException("not implemented"); // FIXME
  }
}
