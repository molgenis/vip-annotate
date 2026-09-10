package org.molgenis.vipannotate.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class IntValueWriter {
  private final IntWriteValueFunction writeValueFunction;
  @Getter private final int valueSizeInBytes;

  public void write(int value, MemoryBuffer memoryBuffer, int index) {
    writeValueFunction.apply(value, memoryBuffer, index);
  }
}
