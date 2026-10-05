package org.molgenis.vipannotate.annotation.spec;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public sealed interface AnnotationSpec
    permits EnumAnnotationSpec, EnumSetAnnotationSpec, FloatAnnotationSpec, IntAnnotationSpec {
  @Nullable String description();

  static void writeTo(BinaryWriter writer, AnnotationSpec spec) {
    switch (spec) {
      case EnumAnnotationSpec enumSpec -> {
        writer.writeEnum(Type.ENUM);
        enumSpec.writeTo(writer);
      }
      case EnumSetAnnotationSpec enumSetSpec -> {
        writer.writeEnum(Type.ENUM_SET);
        enumSetSpec.writeTo(writer);
      }
      case FloatAnnotationSpec floatSpec -> {
        writer.writeEnum(Type.FLOAT);
        floatSpec.writeTo(writer);
      }
      case IntAnnotationSpec intSpec -> {
        writer.writeEnum(Type.INT);
        intSpec.writeTo(writer);
      }
    }
  }

  static AnnotationSpec readFrom(BinaryReader reader) {
    return switch (reader.readEnum(Type.class)) {
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
