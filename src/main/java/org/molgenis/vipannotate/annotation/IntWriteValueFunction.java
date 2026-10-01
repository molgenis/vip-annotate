package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryWriter;

@FunctionalInterface
public interface IntWriteValueFunction {
  void apply(long value, BinaryWriter binaryWriter);
}
