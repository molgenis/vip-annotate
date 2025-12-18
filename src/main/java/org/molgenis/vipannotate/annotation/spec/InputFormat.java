package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = BedInputFormat.class, name = "bed"),
  @JsonSubTypes.Type(value = TsvInputFormat.class, name = "tsv"),
  @JsonSubTypes.Type(value = VcfInputFormat.class, name = "vcf")
})
public sealed interface InputFormat permits BedInputFormat, TsvInputFormat, VcfInputFormat {}
