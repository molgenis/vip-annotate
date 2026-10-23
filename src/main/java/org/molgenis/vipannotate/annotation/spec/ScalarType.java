package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public sealed interface ScalarType permits FloatType, IntType {
  int getBitSize();

  default int getByteSize() {
    return getBitSize() / Byte.SIZE;
  }

  static void writeTo(MemoryEncoder writer, ScalarType scalarType) {
    switch (scalarType) {
      case FloatType floatType -> {
        writer.putEnum(Type.FLOAT);
        writer.putEnum(floatType);
      }
      case IntType intType -> {
        writer.putEnum(Type.INT);
        writer.putEnum(intType);
      }
    }
  }

  static ScalarType readFrom(MemoryDecoder reader) {
    return switch (reader.getEnum(ScalarType.Type.class)) {
      case FLOAT -> reader.getEnum(FloatType.class);
      case INT -> reader.getEnum(IntType.class);
    };
  }

  enum Type {
    FLOAT,
    INT
  }
}
