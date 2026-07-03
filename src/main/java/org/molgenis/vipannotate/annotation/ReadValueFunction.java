package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface ReadValueFunction {
  int apply(MemoryBuffer memoryBuffer, int index);
}
