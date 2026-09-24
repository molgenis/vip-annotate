package org.molgenis.vipannotate.annotation.resolved;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.spec.AnnotationSelector;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

public class ResolvedAnnotationDbSpecSerializer {

  public MemoryBuffer serialize(ResolvedAnnotationDbSpec spec) {
    MemoryBuffer memBuffer = MemoryBuffer.allocate(32 * 1024);
    writeString(spec.specVersion(), memBuffer);
    writeString(spec.specId(), memBuffer);
    writeStringNullable(spec.specDescription(), memBuffer);
    writeSchema(spec.annotationSchema(), memBuffer);
    return memBuffer;
  }

  private static void writeSchema(
      ResolvedAnnotationSchema resolvedAnnotationSchema, MemoryBuffer memBuffer) {
    switch (resolvedAnnotationSchema.annotationType()) {
      case INTERVAL -> memBuffer.putByte((byte) 0);
      case POSITION -> memBuffer.putByte((byte) 1);
      case SEQUENCE_VARIANT -> memBuffer.putByte((byte) 2);
    }

    EnumSet<SequenceVariantType> sequenceVariantTypes =
        resolvedAnnotationSchema.supportedVariantTypes();
    memBuffer.putVarUnsignedInt(sequenceVariantTypes.size());
    for (SequenceVariantType sequenceVariantType : sequenceVariantTypes) {
      switch (sequenceVariantType) {
        case SNV -> memBuffer.putByte((byte) 0);
        case MNV -> memBuffer.putByte((byte) 1);
        case INDEL -> memBuffer.putByte((byte) 2);
        case INSERTION -> memBuffer.putByte((byte) 3);
        case DELETION -> memBuffer.putByte((byte) 4);
        case STRUCTURAL -> memBuffer.putByte((byte) 5);
        case OTHER -> memBuffer.putByte((byte) 6);
      }
    }

    writeSpecs(resolvedAnnotationSchema.annotationSpecs(), memBuffer);

    AnnotationSelector annotationSelector = resolvedAnnotationSchema.annotationSelector();
    switch (annotationSelector) {
      case MAX_VALUE -> memBuffer.putByte((byte) 0);
    }
  }

  private static void writeSpecs(ResolvedAnnotationSpecs specs, MemoryBuffer memBuffer) {
    memBuffer.putVarUnsignedInt(specs.size());
    specs.forEach(
        (annotationId, spec) -> {
          writeString(annotationId, memBuffer);
          writeSpec(spec, memBuffer);
        });
  }

  private static void writeSpec(ResolvedAnnotationSpec spec, MemoryBuffer memBuffer) {
    switch (spec) {
      case ResolvedEnumAnnotationSpec enumSpec -> {
        memBuffer.putByte((byte) 0);
        writeSpecEnum(enumSpec, memBuffer);
      }
      case ResolvedEnumSetAnnotationSpec enumSetSpec -> {
        memBuffer.putByte((byte) 1);
        writeSpecEnumSet(enumSetSpec, memBuffer);
      }
      case ResolvedFloatAnnotationSpec floatSpec -> {
        memBuffer.putByte((byte) 2);
        writeSpecFloat(floatSpec, memBuffer);
      }
      case ResolvedIntAnnotationSpec intSpec -> {
        memBuffer.putByte((byte) 3);
        writeSpecInt(intSpec, memBuffer);
      }
    }
  }

  private static void writeSpecEnum(ResolvedEnumAnnotationSpec enumSpec, MemoryBuffer memBuffer) {
    String description = enumSpec.description();
    String[] values = enumSpec.values();
    boolean nullable = enumSpec.nullable();

    writeStringNullable(description, memBuffer);
    writeStringArray(values, memBuffer);
    writeBoolean(nullable, memBuffer);
  }

  private static void writeBoolean(boolean b, MemoryBuffer memBuffer) {
    memBuffer.putByte((byte) (b ? 1 : 0));
  }

  private static void writeString(String str, MemoryBuffer memBuffer) {
    memBuffer.putByteArray(str.getBytes(UTF_8));
  }

  private static void writeStringNullable(@Nullable String str, MemoryBuffer memBuffer) {
    if (str != null) {
      writeBoolean(true, memBuffer);
      writeString(str, memBuffer);
    } else {
      writeBoolean(false, memBuffer);
      memBuffer.putByte((byte) 0);
    }
  }

  private static void writeStringArray(String[] values, MemoryBuffer memBuffer) {
    memBuffer.putVarUnsignedInt(values.length);
    for (String value : values) {
      writeString(value, memBuffer);
    }
  }

  private static void writeSpecEnumSet(
      ResolvedEnumSetAnnotationSpec enumSetSpec, MemoryBuffer memBuffer) {
    String description = enumSetSpec.description();
    String[] values = enumSetSpec.values();

    writeStringNullable(description, memBuffer);
    writeStringArray(values, memBuffer);
  }

  private static void writeSpecFloat(
      ResolvedFloatAnnotationSpec floatSpec, MemoryBuffer memBuffer) {
    writeStringNullable(floatSpec.description(), memBuffer);
    switch (floatSpec.storageType()) {
      case FloatType floatType -> {
        memBuffer.putByte((byte) 0);
        switch (floatType) {
          case F32 -> memBuffer.putByte((byte) 0);
          case F64 -> memBuffer.putByte((byte) 1);
        }
      }
      case IntType intType -> {
        memBuffer.putByte((byte) 1);
        writeIntType(intType, memBuffer);
      }
    }

    switch (floatSpec.floatEncoding()) {
      case NullableFloatEncoding _ -> memBuffer.putByte((byte) 0);
      case PlainFloatEncoding _ -> memBuffer.putByte((byte) 1);
      case QuantizedEncoding quantizedEncoding -> {
        memBuffer.putByte((byte) 2);
        QuantizedEncoding.Range range = quantizedEncoding.range();
        QuantizedEncoding.Levels levels = quantizedEncoding.levels();
        Integer nullCode = quantizedEncoding.nullCode();

        writeDouble(range.min(), memBuffer);
        writeDouble(range.max(), memBuffer);
        memBuffer.putInt(levels.min());
        memBuffer.putInt(levels.max());
        writeIntegerNullable(nullCode, memBuffer);
      }
    }
  }

  private static void writeIntegerNullable(@Nullable Integer i, MemoryBuffer memBuffer) {
    if (i != null) {
      writeBoolean(true, memBuffer);
      memBuffer.putInt(i);
    } else {
      writeBoolean(false, memBuffer);
    }
  }

  private static void writeDouble(double d, MemoryBuffer memBuffer) {
    memBuffer.putLong(Double.doubleToLongBits(d));
  }

  private static void writeSpecInt(ResolvedIntAnnotationSpec intSpec, MemoryBuffer memBuffer) {
    writeStringNullable(intSpec.description(), memBuffer);
    writeIntType(intSpec.storageType(), memBuffer);
    IntEncoding intEncoding = intSpec.intEncoding();
    switch (intEncoding) {
      case NullableIntEncoding _ -> memBuffer.putByte((byte) 0);
      case OffsetIntEncoding offsetIntEncoding -> {
        memBuffer.putByte((byte) 1);
        memBuffer.putInt(offsetIntEncoding.offset());
      }
      case OffsetNullableIntEncoding offsetNullableIntEncoding -> {
        memBuffer.putByte((byte) 2);
        memBuffer.putInt(offsetNullableIntEncoding.offset());
      }
      case PlainIntEncoding _ -> memBuffer.putByte((byte) 3);
    }
  }

  private static void writeIntType(IntType intType, MemoryBuffer memBuffer) {
    switch (intType) {
      case I8 -> memBuffer.putByte((byte) 0);
      case I16 -> memBuffer.putByte((byte) 1);
      case I32 -> memBuffer.putByte((byte) 2);
      case I64 -> memBuffer.putByte((byte) 3);
      case U8 -> memBuffer.putByte((byte) 4);
      case U16 -> memBuffer.putByte((byte) 5);
      case U32 -> memBuffer.putByte((byte) 6);
      case U64 -> memBuffer.putByte((byte) 7);
    }
  }

  public static ResolvedAnnotationDbSpecSerializer create() {
    return new ResolvedAnnotationDbSpecSerializer();
  }
}
