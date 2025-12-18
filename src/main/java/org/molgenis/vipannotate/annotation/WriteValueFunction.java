package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface WriteValueFunction {
  void apply(int value, MemoryBuffer memoryBuffer, int index);
}
