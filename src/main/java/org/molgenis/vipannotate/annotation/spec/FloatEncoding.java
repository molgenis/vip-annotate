package org.molgenis.vipannotate.annotation.spec;

import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public sealed interface FloatEncoding permits PlainFloatEncoding, QuantizedEncoding {
  void writeTo(MemoryEncoder memEncoder);

  static void writeTo(MemoryEncoder memEncoder, FloatEncoding floatEncoding) {
    memEncoder.putEnum(
        switch (floatEncoding) {
          case PlainFloatEncoding _ -> Type.PLAIN;
          case QuantizedEncoding _ -> Type.QUANTIZED;
        });
    floatEncoding.writeTo(memEncoder);
  }

  static FloatEncoding readFrom(MemoryDecoder reader) {
    return switch (reader.getEnum(Type.class)) {
      case PLAIN -> new PlainFloatEncoding();
      case QUANTIZED -> QuantizedEncoding.readFrom(reader);
    };
  }

  enum Type {
    PLAIN,
    QUANTIZED
  }
}
