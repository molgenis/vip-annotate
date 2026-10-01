package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class FloatAnnotationEncoder implements AnnotationEncoder<FloatAnnotation> {
  private final FloatValueWriter floatValueWriter;

  @Override
  public void encodeInto(FloatAnnotation annotation, BinaryWriter binaryWriter) {
    floatValueWriter.write(annotation.getValue(), binaryWriter);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return floatValueWriter.getValueSizeInBytes();
  }
}
