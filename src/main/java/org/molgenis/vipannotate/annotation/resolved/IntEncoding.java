package org.molgenis.vipannotate.annotation.resolved;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public sealed interface IntEncoding
    permits NullableIntEncoding, OffsetIntEncoding, OffsetNullableIntEncoding, PlainIntEncoding {
  static void writeTo(BinaryWriter writer, IntEncoding intEncoding) {
    switch (intEncoding) {
      case NullableIntEncoding _ -> writer.writeEnum(Type.NULLABLE);
      case OffsetIntEncoding offsetEncoding -> {
        writer.writeEnum(Type.OFFSET);
        offsetEncoding.writeTo(writer);
      }
      case OffsetNullableIntEncoding offsetNullableEncoding -> {
        writer.writeEnum(Type.OFFSET_NULLABLE);
        offsetNullableEncoding.writeTo(writer);
      }
      case PlainIntEncoding _ -> writer.writeEnum(Type.PLAIN);
    }
  }

  static IntEncoding readFrom(BinaryReader reader) {
    return switch (reader.readEnum(Type.class)) {
      case NULLABLE -> new NullableIntEncoding();
      case OFFSET -> OffsetIntEncoding.readFrom(reader);
      case OFFSET_NULLABLE -> OffsetNullableIntEncoding.readFrom(reader);
      case PLAIN -> new PlainIntEncoding();
    };
  }

  enum Type {
    NULLABLE,
    OFFSET,
    OFFSET_NULLABLE,
    PLAIN
  }
}
