package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class NullableFloatAnnotationEncoder implements AnnotationEncoder<NullableFloatAnnotation> {
  private final FloatValueWriter valueWriter;

  @Override
  public void encodeInto(NullableFloatAnnotation annotation, BinaryWriter binaryWriter) {
    double encodedValue = annotation.isNull() ? Double.NaN : annotation.getValue();
    valueWriter.write(encodedValue, binaryWriter);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return valueWriter.getValueSizeInBytes();
  }
}
