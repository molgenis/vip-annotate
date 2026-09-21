package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FloatType.class, name = "floating_point"),
  @JsonSubTypes.Type(value = IntType.class, name = "integer")
})
public sealed interface ScalarType permits FloatType, IntType {
  int getBitSize();

  default int getByteSize() {
    return getBitSize() / Byte.SIZE;
  }
}
