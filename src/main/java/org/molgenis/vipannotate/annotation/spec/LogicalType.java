package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogicalType(
    @JsonProperty(value = "scalar_type", required = true) ScalarType scalarType,
    @JsonProperty("nullable") boolean nullable) {}
