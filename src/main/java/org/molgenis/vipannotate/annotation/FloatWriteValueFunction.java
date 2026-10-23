package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryEncoder;

@FunctionalInterface
public interface FloatWriteValueFunction {
  void apply(double value, MemoryEncoder memEncoder);
}
