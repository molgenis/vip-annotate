package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnumSetLogicalType(@JsonProperty(value = "values", required = true) String[] values)
    implements LogicalType {}
