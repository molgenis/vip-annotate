package org.molgenis.vipannotate.annotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.BinaryWriter;

@RequiredArgsConstructor
public class IntValueWriter {
  private final IntWriteValueFunction writeValueFunction;
  @Getter private final int valueSizeInBytes;

  public void write(long value, BinaryWriter binaryWriter) {
    writeValueFunction.apply(value, binaryWriter);
  }
}
