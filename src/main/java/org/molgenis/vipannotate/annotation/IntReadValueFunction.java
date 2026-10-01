package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryDecoder;

@FunctionalInterface
public interface IntReadValueFunction {
  long apply(MemoryDecoder memDecoder, int index);
}
