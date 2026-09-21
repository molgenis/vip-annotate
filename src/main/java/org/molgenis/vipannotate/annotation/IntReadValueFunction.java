package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface IntReadValueFunction {
  long apply(MemoryBuffer memoryBuffer, int index);
}
