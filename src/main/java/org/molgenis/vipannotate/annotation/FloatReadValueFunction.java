package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryDecoder;

@FunctionalInterface
public interface FloatReadValueFunction {
  double apply(MemoryDecoder memDecoder, int index);
}
