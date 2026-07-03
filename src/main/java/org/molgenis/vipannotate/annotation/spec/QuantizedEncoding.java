package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jspecify.annotations.Nullable;

@JsonTypeName("quantized")
public record QuantizedEncoding(
    @JsonProperty(value = "type", required = true) EncodingType encodingType,
    @JsonProperty(value = "range", required = true) Range range,
    @JsonProperty(value = "levels", required = true) Levels levels,
    @JsonProperty(value = "null_code") @Nullable Integer nullCode)
    implements Encoding {

  public QuantizedEncoding(
      @JsonProperty(value = "range", required = true) Range range,
      @JsonProperty(value = "levels", required = true) Levels levels,
      @JsonProperty(value = "null_code") @Nullable Integer nullCode) {
    this(EncodingType.QUANTIZED, range, levels, nullCode);
  }

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
