package org.molgenis.vipannotate.annotation.resolved;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.SequenceVariantType;
import org.molgenis.vipannotate.annotation.spec.AnnotationSelector;
import org.molgenis.vipannotate.annotation.spec.AnnotationType;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.Maps;

public class ResolvedAnnotationDbSpecDeserializer {

  public ResolvedAnnotationDbSpec deserialize(MemoryBuffer memBuffer) {
    String version = readString(memBuffer);
    String id = readString(memBuffer);
    String description = readStringNullable(memBuffer);
    ResolvedAnnotationSchema annotationSchema = readSchema(memBuffer);
    return new ResolvedAnnotationDbSpec(version, id, description, annotationSchema);
  }

  private static ResolvedAnnotationSchema readSchema(MemoryBuffer memBuffer) {
    AnnotationType annotationType =
        switch (memBuffer.getByte()) {
          case 0 -> AnnotationType.INTERVAL;
          case 1 -> AnnotationType.POSITION;
          case 2 -> AnnotationType.SEQUENCE_VARIANT;
          default -> throw new IllegalStateException();
        };

    EnumSet<SequenceVariantType> sequenceVariantTypes = EnumSet.noneOf(SequenceVariantType.class);
    int size = memBuffer.getVarUnsignedInt();
    for (int i = 0; i < size; ++i) {
      sequenceVariantTypes.add(
          switch (memBuffer.getByte()) {
            case 0 -> SequenceVariantType.SNV;
            case 1 -> SequenceVariantType.MNV;
            case 2 -> SequenceVariantType.INDEL;
            case 3 -> SequenceVariantType.INSERTION;
            case 4 -> SequenceVariantType.DELETION;
            case 5 -> SequenceVariantType.STRUCTURAL;
            case 6 -> SequenceVariantType.OTHER;
            default -> throw new IllegalStateException();
          });
    }

    ResolvedAnnotationSpecs specs = readAnnotations(memBuffer);
    AnnotationSelector annotationSelector =
        switch (memBuffer.getByte()) {
          case 0 -> AnnotationSelector.MAX_VALUE;
          default -> throw new IllegalStateException();
        };

    return new ResolvedAnnotationSchema(
        annotationType, sequenceVariantTypes, specs, annotationSelector);
  }

  private static ResolvedAnnotationSpecs readAnnotations(MemoryBuffer memBuffer) {
    int size = memBuffer.getVarUnsignedInt();
    LinkedHashMap<String, ResolvedAnnotationSpec> specMap =
        Maps.newLinkedHashMapWithExpectedSize(size);
    for (int i = 0; i < size; ++i) {
      String annotationId = readString(memBuffer);
      ResolvedAnnotationSpec spec = readSpec(memBuffer);
      specMap.put(annotationId, spec);
    }
    return new ResolvedAnnotationSpecs(specMap);
  }

  private static ResolvedAnnotationSpec readSpec(MemoryBuffer memBuffer) {
    return switch (memBuffer.getByte()) {
      case 0 -> readSpecEnum(memBuffer);
      case 1 -> readSpecEnumSet(memBuffer);
      case 2 -> readSpecFloat(memBuffer);
      case 3 -> readSpecInt(memBuffer);
      default -> throw new IllegalStateException();
    };
  }

  private static ResolvedEnumAnnotationSpec readSpecEnum(MemoryBuffer memBuffer) {
    String description = readStringNullable(memBuffer);
    String[] values = readStringArray(memBuffer);
    boolean nullable = readBoolean(memBuffer);
    return new ResolvedEnumAnnotationSpec(description, values, nullable);
  }

  private static ResolvedEnumSetAnnotationSpec readSpecEnumSet(MemoryBuffer memBuffer) {
    String description = readStringNullable(memBuffer);
    String[] values = readStringArray(memBuffer);
    return new ResolvedEnumSetAnnotationSpec(description, values);
  }

  private static ResolvedFloatAnnotationSpec readSpecFloat(MemoryBuffer memBuffer) {
    String description = readStringNullable(memBuffer);
    ScalarType storageType = readScalarType(memBuffer);

    FloatEncoding floatEncoding =
        switch (memBuffer.getByte()) {
          case 0 -> new NullableFloatEncoding();
          case 1 -> new PlainFloatEncoding();
          case 2 -> {
            double min = readDouble(memBuffer);
            double max = readDouble(memBuffer);
            QuantizedEncoding.Range range = new QuantizedEncoding.Range(min, max);
            int lvlMin = memBuffer.getInt();
            int lvlMax = memBuffer.getInt();
            QuantizedEncoding.Levels levels = new QuantizedEncoding.Levels(lvlMin, lvlMax);
            Integer nullCode = readIntegerNullable(memBuffer);
            yield new QuantizedEncoding(range, levels, nullCode);
          }
          default -> throw new IllegalStateException();
        };
    return new ResolvedFloatAnnotationSpec(description, storageType, floatEncoding);
  }

  private static ResolvedIntAnnotationSpec readSpecInt(MemoryBuffer memBuffer) {
    String description = readStringNullable(memBuffer);
    IntType storageType = readIntType(memBuffer);

    IntEncoding intEncoding =
        switch (memBuffer.getByte()) {
          case 0 -> new NullableIntEncoding();
          case 1 -> new OffsetIntEncoding(memBuffer.getInt());
          case 2 -> new OffsetNullableIntEncoding(memBuffer.getInt());
          case 3 -> new PlainIntEncoding();
          default -> throw new IllegalStateException();
        };
    return new ResolvedIntAnnotationSpec(description, storageType, intEncoding);
  }

  private static double readDouble(MemoryBuffer memBuffer) {
    return Double.longBitsToDouble(memBuffer.getLong());
  }

  private static String readString(MemoryBuffer memBuffer) {
    return new String(memBuffer.getByteArray(), UTF_8);
  }

  private static @Nullable String readStringNullable(MemoryBuffer memBuffer) {
    boolean b = readBoolean(memBuffer);
    return b ? new String(memBuffer.getByteArray(), UTF_8) : null;
  }

  private static @Nullable Integer readIntegerNullable(MemoryBuffer memBuffer) {
    boolean b = readBoolean(memBuffer);
    return b ? memBuffer.getInt() : null;
  }

  private static String[] readStringArray(MemoryBuffer memBuffer) {
    int size = memBuffer.getVarUnsignedInt();
    String[] strings = new String[size];
    for (int i = 0; i < size; ++i) {
      strings[i] = readString(memBuffer);
    }
    return strings;
  }

  private static boolean readBoolean(MemoryBuffer memBuffer) {
    return memBuffer.getByte() == 1;
  }

  private static ScalarType readScalarType(MemoryBuffer memBuffer) {
    return switch (memBuffer.getByte()) {
      case 0 ->
          switch (memBuffer.getByte()) {
            case 0 -> FloatType.F32;
            case 1 -> FloatType.F64;
            default -> throw new IllegalStateException();
          };
      case 1 -> readIntType(memBuffer);
      default -> throw new IllegalStateException();
    };
  }

  private static IntType readIntType(MemoryBuffer memBuffer) {
    return switch (memBuffer.getByte()) {
      case 0 -> IntType.I8;
      case 1 -> IntType.I16;
      case 2 -> IntType.I32;
      case 3 -> IntType.I64;
      case 4 -> IntType.U8;
      case 5 -> IntType.U16;
      case 6 -> IntType.U32;
      case 7 -> IntType.U64;
      default -> throw new IllegalStateException();
    };
  }

  public static ResolvedAnnotationDbSpecDeserializer create() {
    return new ResolvedAnnotationDbSpecDeserializer();
  }
}
