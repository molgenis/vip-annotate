package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public sealed interface IntEncoding permits OffsetIntEncoding, PlainIntEncoding {
  void writeTo(MemoryEncoder memEncoder);

  static void writeTo(MemoryEncoder memoryEncoder, IntEncoding intEncoding) {
    memoryEncoder.putEnum(
        switch (intEncoding) {
          case OffsetIntEncoding _ -> Type.OFFSET;
          case PlainIntEncoding _ -> Type.PLAIN;
        });
    intEncoding.writeTo(memoryEncoder);
  }

  static IntEncoding readFrom(MemoryDecoder reader) {
    return switch (reader.getEnum(Type.class)) {
      case OFFSET -> OffsetIntEncoding.readFrom(reader);
      case PLAIN -> new PlainIntEncoding();
    };
  }

  enum Type {
    OFFSET,
    PLAIN
  }
}
