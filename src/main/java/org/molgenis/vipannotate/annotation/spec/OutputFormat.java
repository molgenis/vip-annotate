package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = VcfOutputFormat.class, name = "vcf")})
public sealed interface OutputFormat permits VcfOutputFormat {
  @JsonProperty(value = "type", required = true)
  OutputFormatType type();
}
