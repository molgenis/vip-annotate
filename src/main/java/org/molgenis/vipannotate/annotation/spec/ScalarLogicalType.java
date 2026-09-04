package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScalarLogicalType(
    @JsonProperty(value = "scalar_type", required = true) ScalarType scalarType,
    @JsonProperty("nullable") boolean nullable,
    @JsonProperty("range") Range range)
    implements LogicalType {
  public ScalarLogicalType {
    if (range instanceof Range.FloatingPointRange
        && scalarType.getCategory() == ScalarType.Category.INTEGER) {
      throw new IllegalArgumentException("range min/max can't be floating point");
    }
  }
}
