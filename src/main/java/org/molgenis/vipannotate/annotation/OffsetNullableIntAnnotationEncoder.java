package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class OffsetNullableIntAnnotationEncoder
    implements AnnotationEncoder<ScalarAnnotation.NullableIntAnnotation> {
  private final ValueWriter valueWriter;
  private final int offset;

  @Override
  public void initialize(MemoryBuffer memoryBuffer) {
    // FIXME implement initialize(MemoryBuffer memBuffer)
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(
      ScalarAnnotation.NullableIntAnnotation annotation, MemoryBuffer memoryBuffer, int index) {
    int encodedValue = annotation.isNull() ? 0 : offset + annotation.getValue() + 1;
    valueWriter.write(encodedValue, memoryBuffer, index);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return valueWriter.getValueSizeInBytes();
  }
}
