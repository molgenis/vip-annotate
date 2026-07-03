package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({@JsonSubTypes.Type(name = "quantized", value = QuantizedEncoding.class)})
public sealed interface Encoding permits QuantizedEncoding {
  @JsonProperty(value = "type", required = true)
  EncodingType encodingType();
}
