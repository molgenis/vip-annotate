package org.molgenis.vipannotate.annotation.resolved;

import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public sealed interface FloatEncoding
    permits NullableFloatEncoding, PlainFloatEncoding, QuantizedEncoding {
  static void writeTo(BinaryWriter writer, FloatEncoding floatEncoding) {
    switch (floatEncoding) {
      case NullableFloatEncoding _ -> writer.writeEnum(Type.NULLABLE);
      case PlainFloatEncoding _ -> writer.writeEnum(Type.PLAIN);
      case QuantizedEncoding quantizedEncoding -> {
        writer.writeEnum(Type.QUANTIZED);
        quantizedEncoding.writeTo(writer);
      }
    }
  }

  static FloatEncoding readFrom(BinaryReader reader) {
    return switch (reader.readEnum(Type.class)) {
      case NULLABLE -> new NullableFloatEncoding();
      case PLAIN -> new PlainFloatEncoding();
      case QUANTIZED -> QuantizedEncoding.readFrom(reader);
    };
  }

  enum Type {
    NULLABLE,
    PLAIN,
    QUANTIZED
  }
}
