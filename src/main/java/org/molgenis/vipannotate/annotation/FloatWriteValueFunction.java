package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryWriter;

@FunctionalInterface
public interface FloatWriteValueFunction {
  void apply(double value, BinaryWriter binaryWriter);
}
