package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryDecoder;
import org.molgenis.vipannotate.serialization.MemoryEncoder;

public sealed interface AnnotationSpec
    permits EnumAnnotationSpec, EnumSetAnnotationSpec, FloatAnnotationSpec, IntAnnotationSpec {
  @Nullable String description();

  boolean nullable();

  void writeTo(MemoryEncoder memEncoder);

  static void writeTo(MemoryEncoder writer, AnnotationSpec spec) {
    writer.putEnum(
        switch (spec) {
          case EnumAnnotationSpec _ -> Type.ENUM;
          case EnumSetAnnotationSpec _ -> Type.ENUM_SET;
          case FloatAnnotationSpec _ -> Type.FLOAT;
          case IntAnnotationSpec _ -> Type.INT;
        });
    spec.writeTo(writer);
  }

  static AnnotationSpec readFrom(MemoryDecoder reader) {
    return switch (reader.getEnum(Type.class)) {
      case ENUM -> EnumAnnotationSpec.readFrom(reader);
      case ENUM_SET -> EnumSetAnnotationSpec.readFrom(reader);
      case FLOAT -> FloatAnnotationSpec.readFrom(reader);
      case INT -> IntAnnotationSpec.readFrom(reader);
    };
  }

  enum Type {
    ENUM,
    ENUM_SET,
    FLOAT,
    INT
  }
}
