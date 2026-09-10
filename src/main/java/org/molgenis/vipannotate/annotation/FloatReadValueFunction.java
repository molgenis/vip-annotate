package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryBuffer;

@FunctionalInterface
public interface FloatReadValueFunction {
  double apply(MemoryBuffer memoryBuffer, int index);
}
