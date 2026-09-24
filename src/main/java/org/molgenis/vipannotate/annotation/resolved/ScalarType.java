package org.molgenis.vipannotate.annotation.resolved;

public sealed interface ScalarType permits FloatType, IntType {
  int getBitSize();

  default int getByteSize() {
    return getBitSize() / Byte.SIZE;
  }
}
