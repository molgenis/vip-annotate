package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class OffsetIntAnnotationEncoder implements AnnotationEncoder<IntAnnotation> {
  private final IntValueWriter intValueWriter;
  private final int offset;

  @Override
  public void encodeInto(IntAnnotation annotation, MemoryEncoder memEncoder) {
    if (annotation.isMissingValue()) {
      throw new IllegalArgumentException();
    }
    intValueWriter.write(offset + annotation.getValue(), memEncoder);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
