package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.serialization.BinaryReader;

@FunctionalInterface
public interface IntReadValueFunction {
  long apply(BinaryReader binaryReader, int index);
}
