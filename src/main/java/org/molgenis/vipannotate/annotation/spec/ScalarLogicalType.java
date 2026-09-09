package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

// keep in sync with
// src/main/resources/META-INF/native-image/org.molgenis/vip-annotate/reachability-metadata.json
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
