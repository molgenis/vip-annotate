package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationDecoder implements AnnotationDecoder<ScalarAnnotation> {
  private final Quantizer quantizer;
  private final IntReadValueFunction intReadValueFunction;
  @Nullable private final Integer nullValue;

  @Override
  public void decodeInto(
      BinaryReader binaryReader, int annotationIndex, ScalarAnnotation annotation) {
    long quantizedValue = intReadValueFunction.apply(binaryReader, annotationIndex);

    // FIXME prevent casting
    if (nullValue != null && quantizedValue == nullValue) {
      ((NullableFloatAnnotation) annotation).reset();
    } else {
      double value = quantizer.dequantize(quantizedValue);
      if (nullValue != null) {
        ((NullableFloatAnnotation) annotation).reset(value);
      } else {
        ((FloatAnnotation) annotation).reset(value);
      }
    }
  }
}
