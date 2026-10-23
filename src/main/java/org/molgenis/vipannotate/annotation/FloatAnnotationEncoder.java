package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class FloatAnnotationEncoder implements AnnotationEncoder<FloatAnnotation> {
  private final FloatValueWriter floatValueWriter;

  @Override
  public void encodeInto(FloatAnnotation annotation, MemoryEncoder memEncoder) {
    if (annotation.isMissingValue()) {
      throw new IllegalArgumentException();
    }
    floatValueWriter.write(annotation.getValue(), memEncoder);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return floatValueWriter.getValueSizeInBytes();
  }
}
