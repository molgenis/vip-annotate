package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;
import org.molgenis.vipannotate.util.Quantizer;

// TODO merge (Nullable)DoubleAnnotation and change to AnnotationEncoder<DoubleAnnotation>
@RequiredArgsConstructor
public class QuantizedAnnotationEncoder implements AnnotationEncoder<ScalarAnnotation> {
  private final Quantizer quantizer;
  private final IntValueWriter intValueWriter;
  @Nullable private final Integer nullValue;

  @Override
  public void encodeInto(ScalarAnnotation annotation, BinaryWriter binaryWriter) {
    switch (annotation) {
      case FloatAnnotation floatAnnotation -> encodeInto(floatAnnotation, binaryWriter);
      case NullableFloatAnnotation nullableFloatAnnotation ->
          encodeInto(nullableFloatAnnotation, binaryWriter);
      default -> throw new IllegalStateException("Unexpected value: %s".formatted(annotation));
    }
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }

  private void encodeInto(FloatAnnotation annotation, BinaryWriter binaryWriter) {
    long quantizedValue = quantizer.quantize(annotation.getValue());
    intValueWriter.write(quantizedValue, binaryWriter);
  }

  private void encodeInto(NullableFloatAnnotation annotation, BinaryWriter binaryWriter) {
    long quantizedValue;
    if (annotation.isNull()) {
      if (nullValue == null) {
        throw new IllegalStateException();
      }
      quantizedValue = nullValue;
    } else {
      quantizedValue = quantizer.quantize(annotation.getValue());
    }
    intValueWriter.write(quantizedValue, binaryWriter);
  }
}
