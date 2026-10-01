package org.molgenis.vipannotate.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class IntValueWriter {
  private final IntWriteValueFunction writeValueFunction;
  @Getter private final int valueSizeInBytes;

  public void write(long value, MemoryEncoder memEncoder) {
    writeValueFunction.apply(value, memEncoder);
  }
}
