package org.molgenis.vipannotate.annotation;

import java.util.Map;
import org.molgenis.vipannotate.annotation.spec.*;

public class AnnotationDbSpecFormatter {
  private AnnotationDbSpecFormatter() {}

  public static String format(AnnotationDbSpec spec) {
    StringBuilder builder = new StringBuilder();
    builder.append("  %-14s:  %s (%s)\n".formatted("name", spec.id(), spec.version()));
    if (spec.description() != null) {
      builder.append("  %-14s:  %s\n".formatted("description", spec.description()));
    }

    AnnotationsSpec annotationsSpec = spec.annotationsSpec();
    builder.append(
        "  %-14s:  type=%s\n".formatted("annotations", annotationsSpec.annotationType()));
    annotationsSpec.forEach((key, value) -> formatAnnotationSpec(builder, key, value));
    builder.append("  %-14s:\n".formatted("partitioning"));
    formatPartitioningSpec(builder, spec.partitioningSpec());

    if (!builder.isEmpty()) {
      builder.deleteCharAt(builder.length() - 1);
    }
    return builder.toString();
  }

  private static void formatPartitioningSpec(
      StringBuilder builder, Map<String, PositionBinSpec> partitioningSpec) {
    partitioningSpec.forEach(
        (contigId, positionBinSpec) ->
            builder.append(
                "    %-12s:  pos_offset=%-10d  pos_length=%-10d  pos_bits=%d\n"
                    .formatted(
                        contigId,
                        positionBinSpec.offset(),
                        positionBinSpec.length(),
                        positionBinSpec.bits())));
  }

  private static void formatAnnotationSpec(
      StringBuilder stringBuilder, String name, AnnotationSpec spec) {
    stringBuilder.append("    %-12s:  ".formatted(name));
    switch (spec) {
      case EnumAnnotationSpec _ ->
          stringBuilder.append("type=%-16s  storage_type=%-12s".formatted("enum", "bit-packing"));
      case EnumSetAnnotationSpec _ ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s".formatted("enum_set", "bit-packing"));
      case FloatAnnotationSpec floatSpec ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s"
                  .formatted("floating_point", floatSpec.storageType()));
      case IntAnnotationSpec intSpec ->
          stringBuilder.append(
              "type=%-16s  storage_type=%-12s".formatted("integer", intSpec.storageType()));
    }

    if (spec.description() != null) {
      stringBuilder.append("  description=%s".formatted(spec.description()));
    }
    stringBuilder.append('\n');
  }
}
