package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.NullableIntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class NullableIntAnnotationEncoder implements AnnotationEncoder<NullableIntAnnotation> {
  private final IntValueWriter intValueWriter;

  @Override
  public void initialize(MemoryBuffer memoryBuffer) {
    // FIXME implement initialize(MemoryBuffer memBuffer)
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(NullableIntAnnotation annotation, MemoryBuffer memoryBuffer, int index) {
    int encodedValue;
    if (annotation.isNull()) {
      encodedValue = 0;
    } else {
      int value = annotation.getValue();
      encodedValue = value < 0 ? value : value + 1;
    }
    intValueWriter.write(encodedValue, memoryBuffer, index);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return intValueWriter.getValueSizeInBytes();
  }
}
