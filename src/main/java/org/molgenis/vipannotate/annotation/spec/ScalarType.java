package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public sealed interface ScalarType permits FloatType, IntType {
  int getBitSize();

  default int getByteSize() {
    return getBitSize() / Byte.SIZE;
  }

  static void writeTo(BinaryWriter writer, ScalarType scalarType) {
    switch (scalarType) {
      case FloatType floatType -> {
        writer.writeEnum(Type.FLOAT);
        writer.writeEnum(floatType);
      }
      case IntType intType -> {
        writer.writeEnum(Type.INT);
        writer.writeEnum(intType);
      }
    }
  }

  static ScalarType readFrom(BinaryReader reader) {
    return switch (reader.readEnum(ScalarType.Type.class)) {
      case FLOAT -> reader.readEnum(FloatType.class);
      case INT -> reader.readEnum(IntType.class);
    };
  }

  enum Type {
    FLOAT,
    INT
  }
}
