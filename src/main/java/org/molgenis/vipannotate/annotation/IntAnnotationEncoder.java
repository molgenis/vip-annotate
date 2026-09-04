package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.ScalarAnnotation.IntAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class IntAnnotationEncoder implements AnnotationEncoder<IntAnnotation> {
  private final ValueWriter valueWriter;

  @Override
  public void initialize(MemoryBuffer memoryBuffer) {
    // FIXME implement initialize(MemoryBuffer memBuffer)
    System.err.println("FIXME implement initialize(MemoryBuffer memBuffer)");
  }

  @Override
  public void encodeInto(IntAnnotation annotation, MemoryBuffer memoryBuffer, int index) {
    valueWriter.write(annotation.getValue(), memoryBuffer, index);
  }

  @Override
  public long getEncodedSizeInBytes() {
    return valueWriter.getValueSizeInBytes();
  }
}
