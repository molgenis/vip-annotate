package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableFloatAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class NullableFloatAnnotationEncoder implements AnnotationEncoder<NullableFloatAnnotation> {
  private final FloatValueWriter valueWriter;

  @Override
  public void initialize(MemoryBuffer memoryBuffer) {
    // FIXME implement initialize(MemoryBuffer memBuffer)
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(NullableFloatAnnotation annotation, MemoryBuffer memoryBuffer, int index) {
    double encodedValue = annotation.isNull() ? Double.NaN : annotation.getValue();
    valueWriter.write(encodedValue, memoryBuffer, index);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return valueWriter.getValueSizeInBytes();
  }
}
