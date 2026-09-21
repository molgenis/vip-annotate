package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface IntWriteValueFunction {
  void apply(long value, MemoryBuffer memoryBuffer, int index);
}
