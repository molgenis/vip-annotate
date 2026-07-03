package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StorageType(
    @JsonProperty(value = "scalar_type", required = true) ScalarType scalarType) {}
