package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = EnumEncoding.class, name = "enum"),
  @JsonSubTypes.Type(value = QuantizedEncoding.class, name = "quantized")
})
public sealed interface Encoding permits EnumEncoding, QuantizedEncoding {}
