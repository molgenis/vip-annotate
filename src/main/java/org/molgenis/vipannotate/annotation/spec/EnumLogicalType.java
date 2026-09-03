package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnumLogicalType(
    @JsonProperty(value = "values", required = true) String[] values,
    @JsonProperty(value = "nullable") boolean nullable)
    implements LogicalType {}
