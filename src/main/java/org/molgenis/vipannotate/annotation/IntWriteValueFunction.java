package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface IntWriteValueFunction {
  void apply(int value, MemoryBuffer memoryBuffer, int index);
}
