package org.molgenis.vipannotate.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

@RequiredArgsConstructor
public class FloatValueWriter {
  private final FloatWriteValueFunction writeValueFunction;
  @Getter private final int valueSizeInBytes;

  public void write(double value, MemoryEncoder memEncoder) {
    writeValueFunction.apply(value, memEncoder);
  }
}
