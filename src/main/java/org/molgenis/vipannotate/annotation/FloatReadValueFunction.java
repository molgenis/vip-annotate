package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryReader;

@FunctionalInterface
public interface FloatReadValueFunction {
  double apply(BinaryReader binaryReader, int index);
}
