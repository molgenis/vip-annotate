package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.MemoryEncoder;

@FunctionalInterface
public interface IntWriteValueFunction {
  void apply(long value, MemoryEncoder memEncoder);
}
