package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.FloatAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class FloatAnnotationEncoder implements AnnotationEncoder<FloatAnnotation> {
  private final FloatValueWriter floatValueWriter;

  @Override
  public void initialize(MemoryBuffer memoryBuffer) {
    // FIXME implement initialize(MemoryBuffer memBuffer)
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(FloatAnnotation annotation, MemoryBuffer memoryBuffer, int index) {
    floatValueWriter.write(annotation.getValue(), memoryBuffer, index);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return floatValueWriter.getValueSizeInBytes();
  }
}
