package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Range.IntegerRange.class, name = "integer"),
  @JsonSubTypes.Type(value = Range.FloatingPointRange.class, name = "floating_point")
})
public sealed interface Range permits Range.IntegerRange, Range.FloatingPointRange {
  record IntegerRange(long min, long max) implements Range {
    public IntegerRange {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }

  record FloatingPointRange(double min, double max) implements Range {
    public FloatingPointRange {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }
}
