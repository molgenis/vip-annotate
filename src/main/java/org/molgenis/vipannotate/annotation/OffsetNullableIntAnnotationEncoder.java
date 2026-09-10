package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class OffsetNullableIntAnnotationEncoder
    implements AnnotationEncoder<NullableIntAnnotation> {
  private final IntValueWriter intValueWriter;
  private final int offset;

  @Override
  public void encodeInto(NullableIntAnnotation annotation, BinaryWriter binaryWriter) {
    long encodedValue = annotation.isNull() ? 0 : offset + annotation.getValue() + 1;
    intValueWriter.write(encodedValue, binaryWriter);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
