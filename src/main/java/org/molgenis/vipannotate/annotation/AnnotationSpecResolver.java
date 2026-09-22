package org.molgenis.vipannotate.annotation;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.resolved.*;
import org.molgenis.vipannotate.annotation.spec.EnumAnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.EnumSetAnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.FloatAnnotationSpec;
import org.molgenis.vipannotate.annotation.spec.IntAnnotationSpec;
import org.molgenis.vipannotate.util.Maps;

@RequiredArgsConstructor
public class AnnotationSpecResolver {
  public ResolvedAnnotationSpecs resolve(AnnotationAnalyses analyses) {
    Map<String, ResolvedAnnotationSpec> resolvedSpecsMap =
        Maps.newLinkedHashMapWithExpectedSize(analyses.size());

    analyses.forEach(
        (annotationDatasetId, analysis) ->
            resolvedSpecsMap.put(annotationDatasetId, resolve(analysis)));

    return new ResolvedAnnotationSpecs(resolvedSpecsMap);
  }

  private ResolvedAnnotationSpec resolve(AnnotationAnalysis annotationAnalysis) {
    return switch (annotationAnalysis) {
      case EnumAnnotationAnalysis analysis -> resolve(analysis.annotationSpec(), analysis.stats());
      case EnumSetAnnotationAnalysis analysis ->
          resolve(analysis.annotationSpec(), analysis.stats());
      case FloatAnnotationAnalysis analysis -> resolve(analysis.annotationSpec(), analysis.stats());
      case IntAnnotationAnalysis analysis -> resolve(analysis.annotationSpec(), analysis.stats());
    };
  }

  private ResolvedEnumAnnotationSpec resolve(EnumAnnotationSpec spec, EnumAnnotationStats stats) {
    return new ResolvedEnumAnnotationSpec(spec.values(), stats.nullCount() > 0);
  }

  private ResolvedEnumSetAnnotationSpec resolve(
      EnumSetAnnotationSpec spec, EnumSetAnnotationStats ignoredStats) {
    return new ResolvedEnumSetAnnotationSpec(spec.values());
  }

  private ResolvedFloatAnnotationSpec resolve(
      FloatAnnotationSpec spec, FloatAnnotationStats stats) {
    Integer nullCode = stats.nullCount() > 0 ? 0 : null;
    int lvlMin = nullCode != null ? 1 : 0;
    int lvlMax = Math.powExact(2, Short.SIZE) - 1;
    FloatEncoding floatEncoding =
        new QuantizedEncoding(
            new QuantizedEncoding.Range(stats.min(), stats.max()),
            new QuantizedEncoding.Levels(lvlMin, lvlMax),
            nullCode);
    return new ResolvedFloatAnnotationSpec(IntType.U16, floatEncoding);
  }

  private ResolvedIntAnnotationSpec resolve(IntAnnotationSpec spec, IntAnnotationStats stats) {

    long min = stats.min();
    long max = stats.max();
    boolean nullable = stats.nullCount() > 0;

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

      return new ResolvedIntAnnotationSpec(requireNonNull(offsetType), encoding);
    }

    IntEncoding encoding = nullable ? new NullableIntEncoding() : new PlainIntEncoding();
    return new ResolvedIntAnnotationSpec(requireNonNull(plainType), encoding);
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
