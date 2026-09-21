package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = NullableFloatEncoding.class, name = "nullable"),
  @JsonSubTypes.Type(value = PlainFloatEncoding.class, name = "plain"),
  @JsonSubTypes.Type(value = QuantizedEncoding.class, name = "quantized")
})
public sealed interface FloatEncoding
    permits NullableFloatEncoding, PlainFloatEncoding, QuantizedEncoding {}
