package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.def.*;
import org.molgenis.vipannotate.annotation.spec.*;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public class AnnotationsSpecResolver {
  public Map<String, AnnotationSpec> resolve(
      Map<String, FieldAnalysis> annotationAnalyses, AnnotationType annotationType) {
    Map<String, AnnotationSpec> annotationSpecMap =
        Maps.newLinkedHashMapWithExpectedSize(annotationAnalyses.size());

    annotationAnalyses.forEach(
        (annotationDatasetId, analysis) ->
            annotationSpecMap.put(annotationDatasetId, resolve(analysis, annotationType)));

    return annotationSpecMap;
  }

  private AnnotationSpec resolve(FieldAnalysis fieldAnalysis, AnnotationType annotationType) {
    return switch (fieldAnalysis) {
      case EnumFieldAnalysis analysis ->
          resolve(analysis.annotationSpec(), analysis.stats(), annotationType);
      case EnumSetFieldAnalysis analysis ->
          resolve(analysis.annotationSpec(), analysis.stats(), annotationType);
      case FloatFieldAnalysis analysis ->
          resolve(analysis.annotationSpec(), analysis.stats(), annotationType);
      case IntFieldAnalysis analysis ->
          resolve(analysis.annotationSpec(), analysis.stats(), annotationType);
    };
  }

  private EnumAnnotationSpec resolve(
      EnumAnnotationDef spec, EnumAnnotationStats stats, AnnotationType annotationType) {
    EnumValue[] enumValues = spec.values();
    return new EnumAnnotationSpec(
        resolveEnumSpecDescription(spec.description(), enumValues),
        Arrays.stream(enumValues).map(EnumValue::value).toArray(String[]::new),
        stats.nullCount() > 0 || annotationType == AnnotationType.POSITION);
  }

  private static @NonNull String resolveEnumSpecDescription(
      @Nullable String description, EnumValue[] enumValues) {
    // resolve description from spec description and enum value descriptions
    StringBuilder stringBuilder = new StringBuilder();
    if (description != null) {
      stringBuilder.append(description);
      if (description.charAt(description.length() - 1) != '.') {
        stringBuilder.append('.');
      }
      stringBuilder.append(' ');
    }

    for (EnumValue enumValue : enumValues) {
      stringBuilder.append(enumValue.value());
      if (enumValue.description() != null) {
        stringBuilder.append('=').append(enumValue.description());
      }
      stringBuilder.append(", ");
    }
    stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
    return stringBuilder.toString();
  }

  private EnumSetAnnotationSpec resolve(
      EnumSetAnnotationDef spec,
      EnumSetAnnotationStats ignoredStats,
      AnnotationType ignoredAnnotationType) {
    EnumValue[] enumValues = spec.values();
    return new EnumSetAnnotationSpec(
        resolveEnumSpecDescription(spec.description(), enumValues),
        Arrays.stream(enumValues).map(EnumValue::value).toArray(String[]::new));
  }

  private FloatAnnotationSpec resolve(
      FloatAnnotationDef spec, FloatAnnotationStats stats, AnnotationType annotationType) {
    Integer nullCode =
        (stats.nullCount() > 0 || annotationType == AnnotationType.POSITION) ? 0 : null;

    String description = spec.description();
    ScalarType scalarType;
    FloatEncoding floatEncoding;
    switch (spec.floatEncodingType()) {
      case LOSSLESS -> {
        scalarType = FloatType.F64;
        floatEncoding = nullCode != null ? new NullableFloatEncoding() : new PlainFloatEncoding();
      }
      case Q8 -> {
        scalarType = IntType.U8;

        int lvlMin = nullCode != null ? 1 : 0;
        int lvlMax = Math.powExact(2, Byte.SIZE) - 1;
        floatEncoding =
            new QuantizedEncoding(
                new QuantizedEncoding.Range(stats.min(), stats.max()),
                new QuantizedEncoding.Levels(lvlMin, lvlMax),
                nullCode);

        double v = maxQuantizationError(stats.min(), stats.max(), lvlMin, lvlMax);
        if (description == null) {
          description = "maximum deviation ±%f".formatted(v);
        } else {
          description += " (maximum deviation ±%f)".formatted(v);
        }
      }
      case Q16 -> {
        scalarType = IntType.U16;

        int lvlMin = nullCode != null ? 1 : 0;
        int lvlMax = Math.powExact(2, Short.SIZE) - 1;
        floatEncoding =
            new QuantizedEncoding(
                new QuantizedEncoding.Range(stats.min(), stats.max()),
                new QuantizedEncoding.Levels(lvlMin, lvlMax),
                nullCode);

        double v = maxQuantizationError(stats.min(), stats.max(), lvlMin, lvlMax);
        if (description == null) {
          description = "maximum deviation ±%f".formatted(v);
        } else {
          description += " (maximum deviation ±%f)".formatted(v);
        }
      }
      default -> throw new IllegalStateException();
    }
    return new FloatAnnotationSpec(description, scalarType, floatEncoding);
  }

  private static double maxQuantizationError(double x, double y, int u, int v) {
    return (y - x) / (2.0 * (v - u));
  }

  private IntAnnotationSpec resolve(
      IntAnnotationDef spec, IntAnnotationStats stats, AnnotationType annotationType) {

    long min = stats.min();
    long max = stats.max();
    boolean nullable = stats.nullCount() > 0 || annotationType == AnnotationType.POSITION;

    IntType plainType = resolvePlainIntType(min, max, nullable);
    IntType offsetType = resolveOffsetIntType(min, max, nullable);

    if (plainType == null && offsetType == null) {
      throw new IllegalArgumentException(
          "[%d ,%d] with null values can't be encoded".formatted(min, max));
    }

    boolean useOffset =
        offsetType != null
            && (plainType == null || offsetType.getByteSize() < plainType.getByteSize());

    if (useOffset) {
      long offset = -min;
      // FIXME remove casts
      IntEncoding encoding =
          nullable
              ? new OffsetNullableIntEncoding((int) offset)
              : new OffsetIntEncoding((int) offset);

      return new IntAnnotationSpec(spec.description(), requireNonNull(offsetType), encoding);
    }

    IntEncoding encoding = nullable ? new NullableIntEncoding() : new PlainIntEncoding();
    return new IntAnnotationSpec(spec.description(), requireNonNull(plainType), encoding);
  }

  private static @Nullable IntType resolvePlainIntType(long min, long max, boolean nullable) {
    if (nullable && max == Long.MAX_VALUE) {
      return null;
    }

    long encodedMin = nullable && min >= 0 ? min + 1 : min;
    long encodedMax = nullable && max >= 0 ? max + 1 : max;

    return resolveIntType(encodedMin, encodedMax);
  }

  private static IntType resolveIntType(long min, long max) {
    if (min >= Byte.MIN_VALUE && max <= Byte.MAX_VALUE) {
      return IntType.I8;
    }
    if (min >= 0 && max <= 0xFFL) {
      return IntType.U8;
    }
    if (min >= Short.MIN_VALUE && max <= Short.MAX_VALUE) {
      return IntType.I16;
    }
    if (min >= 0 && max <= 0xFFFFL) {
      return IntType.U16;
    }
    if (min >= Integer.MIN_VALUE && max <= Integer.MAX_VALUE) {
      return IntType.I32;
    }
    if (min >= 0 && max <= 0xFFFFFFFFL) {
      return IntType.U32;
    }
    return IntType.I64;
  }

  private static @Nullable IntType resolveOffsetIntType(long min, long max, boolean nullable) {

    return resolveUnsignedIntType(bitsRequired(min, max, nullable));
  }

  private static @Nullable IntType resolveUnsignedIntType(int bitWidth) {
    if (bitWidth <= Byte.SIZE) {
      return IntType.U8;
    }
    if (bitWidth <= Short.SIZE) {
      return IntType.U16;
    }
    if (bitWidth <= Integer.SIZE) {
      return IntType.U32;
    }
    if (bitWidth <= Long.SIZE) {
      return IntType.U64;
    }
    return null;
  }

  private static int bitsRequired(long min, long max, boolean nullable) {
    if (min > max) {
      throw new IllegalArgumentException("min > max");
    }

    long range = max - min; // intentional overflow

    if (!nullable) {
      return range == 0 ? 0 : Long.SIZE - Long.numberOfLeadingZeros(range);
    }

    // range == 2^64 - 1
    if (range == -1L) {
      return Long.SIZE + 1;
    }

    long maxEncoded = range + 1; // intentional overflow
    return Long.SIZE - Long.numberOfLeadingZeros(maxEncoded);
  }
}
