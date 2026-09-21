package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = NullableIntEncoding.class, name = "nullable"),
  @JsonSubTypes.Type(value = OffsetIntEncoding.class, name = "offset"),
  @JsonSubTypes.Type(value = OffsetNullableIntEncoding.class, name = "offset_nullable"),
  @JsonSubTypes.Type(value = PlainIntEncoding.class, name = "plain")
})
public sealed interface IntEncoding
    permits NullableIntEncoding, OffsetIntEncoding, OffsetNullableIntEncoding, PlainIntEncoding {}
