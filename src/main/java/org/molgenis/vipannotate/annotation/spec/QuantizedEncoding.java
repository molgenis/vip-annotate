package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

public record QuantizedEncoding(
    @JsonProperty(value = "range", required = true) Range range,
    @JsonProperty(value = "levels", required = true) Levels levels,
    @JsonProperty(value = "null_code") @Nullable Integer nullCode)
    implements Encoding {

  public record Range(
      @JsonProperty(value = "min", required = true) double min,
      @JsonProperty(value = "max", required = true) double max) {
    public Range {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }

  public record Levels(
      @JsonProperty(value = "min", required = true) int min,
      @JsonProperty(value = "max", required = true) int max) {
    public Levels {
      if (min > max) {
        throw new IllegalArgumentException("min > max");
      }
    }
  }
}
