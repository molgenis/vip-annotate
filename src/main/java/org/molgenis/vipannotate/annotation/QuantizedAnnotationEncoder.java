package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.DoubleAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableDoubleAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Quantizer;

@RequiredArgsConstructor
public class QuantizedAnnotationEncoder implements AnnotationEncoderTmp<ScalarAnnotation> {
  private final Quantizer quantizer;
  private final WriteValueFunction writeValueFunction;
  @Nullable private final Integer nullValue;

  @Override
  public void initialize(MemoryBuffer memBuffer) {
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(ScalarAnnotation annotation, MemoryBuffer memBuffer, int index) {
    switch (annotation) {
      case DoubleAnnotation doubleAnnotation -> encodeInto(doubleAnnotation, memBuffer, index);
      case NullableDoubleAnnotation nullableDoubleAnnotation ->
          encodeInto(nullableDoubleAnnotation, memBuffer, index);
      default -> throw new IllegalStateException("Unexpected value: %s".formatted(annotation));
    }
  }

  private void encodeInto(DoubleAnnotation annotation, MemoryBuffer memBuffer, int index) {
    int quantizedValue = quantizer.quantize(annotation.getValue());
    writeValueFunction.apply(quantizedValue, memBuffer, index);
  }

  private void encodeInto(NullableDoubleAnnotation annotation, MemoryBuffer memBuffer, int index) {
    int quantizedValue;
    if (annotation.isNull()) {
      if (nullValue == null) {
        throw new IllegalStateException();
      }
      quantizedValue = nullValue;
    } else {
      quantizedValue = quantizer.quantize(annotation.getValue());
    }
    writeValueFunction.apply(quantizedValue, memBuffer, index);
  }
}
