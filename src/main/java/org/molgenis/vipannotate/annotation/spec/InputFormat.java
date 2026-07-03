package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BedInputFormat.class, name = "bed"),
  @JsonSubTypes.Type(value = TsvInputFormat.class, name = "tsv"),
  @JsonSubTypes.Type(value = VcfInputFormat.class, name = "vcf")
})
public sealed interface InputFormat permits BedInputFormat, TsvInputFormat, VcfInputFormat {
  @JsonProperty(value = "type", required = true)
  InputFormatType type();
}
