package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = EnumLogicalType.class, name = "enum"),
  @JsonSubTypes.Type(value = EnumSetLogicalType.class, name = "enum_set"),
  @JsonSubTypes.Type(value = ScalarLogicalType.class, name = "scalar")
})
public sealed interface LogicalType
    permits ScalarLogicalType, EnumLogicalType, EnumSetLogicalType {}
