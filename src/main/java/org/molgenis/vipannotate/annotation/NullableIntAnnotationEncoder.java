package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class NullableIntAnnotationEncoder implements AnnotationEncoder<NullableIntAnnotation> {
  private final IntValueWriter intValueWriter;

  @Override
  public void encodeInto(NullableIntAnnotation annotation, BinaryWriter binaryWriter) {
    long encodedValue;
    if (annotation.isNull()) {
      encodedValue = 0;
    } else {
      long value = annotation.getValue();
      encodedValue = value < 0 ? value : value + 1;
    }
    intValueWriter.write(encodedValue, binaryWriter);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
