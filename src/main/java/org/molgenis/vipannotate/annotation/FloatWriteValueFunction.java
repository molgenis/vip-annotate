package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface FloatWriteValueFunction {
  void apply(double value, MemoryBuffer memoryBuffer, int index);
}
