package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationDecoder implements AnnotationDecoder<ScalarAnnotation> {
  private final Quantizer quantizer;
  private final IntReadValueFunction intReadValueFunction;
  @Nullable private final Integer nullValue;

  @Override
  public ScalarAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    long quantizedValue = intReadValueFunction.apply(memBuffer, annotationIndex);

    ScalarAnnotation scalarAnnotation;
    if (nullValue != null && quantizedValue == nullValue) {
      scalarAnnotation = new NullableFloatAnnotation();
    } else {
      double value = quantizer.dequantize(quantizedValue);
      scalarAnnotation =
          nullValue != null ? new NullableFloatAnnotation(value) : new FloatAnnotation(value);
    }
    return scalarAnnotation;
  }

  @Override
  public void decodeInto(MemoryBuffer memBuffer, int annotationIndex, ScalarAnnotation annotation) {
    throw new RuntimeException("not implemented"); // FIXME
  }
}
