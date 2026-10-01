package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class IntAnnotationEncoder implements AnnotationEncoder<IntAnnotation> {
  private final IntValueWriter intValueWriter;

  @Override
  public void encodeInto(IntAnnotation annotation, MemoryEncoder memEncoder) {
    if (annotation.isMissingValue()) {
      throw new IllegalArgumentException();
    }
    intValueWriter.write(annotation.getValue(), memEncoder);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
