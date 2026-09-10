package org.molgenis.vipannotate.annotation.resolved;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.BinaryReader;
import org.molgenis.vipannotate.serialization.BinaryWriter;

public sealed interface ResolvedAnnotationSpec
    permits ResolvedEnumAnnotationSpec,
        ResolvedEnumSetAnnotationSpec,
        ResolvedFloatAnnotationSpec,
        ResolvedIntAnnotationSpec {
  @Nullable String description();

  static void writeTo(BinaryWriter writer, ResolvedAnnotationSpec spec) {
    switch (spec) {
      case ResolvedEnumAnnotationSpec enumSpec -> {
        writer.writeEnum(Type.ENUM);
        enumSpec.writeTo(writer);
      }
      case ResolvedEnumSetAnnotationSpec enumSetSpec -> {
        writer.writeEnum(Type.ENUM_SET);
        enumSetSpec.writeTo(writer);
      }
      case ResolvedFloatAnnotationSpec floatSpec -> {
        writer.writeEnum(Type.FLOAT);
        floatSpec.writeTo(writer);
      }
      case ResolvedIntAnnotationSpec intSpec -> {
        writer.writeEnum(Type.INT);
        intSpec.writeTo(writer);
      }
    }
  }

  static ResolvedAnnotationSpec readFrom(BinaryReader reader) {
    return switch (reader.readEnum(Type.class)) {
      case ENUM -> ResolvedEnumAnnotationSpec.readFrom(reader);
      case ENUM_SET -> ResolvedEnumSetAnnotationSpec.readFrom(reader);
      case FLOAT -> ResolvedFloatAnnotationSpec.readFrom(reader);
      case INT -> ResolvedIntAnnotationSpec.readFrom(reader);
    };
  }

  enum Type {
    ENUM,
    ENUM_SET,
    FLOAT,
    INT
  }
}
