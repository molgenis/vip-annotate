package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = BedInputFormat.class, name = "bed"),
  @JsonSubTypes.Type(value = TsvInputFormat.class, name = "tsv")
})
public sealed interface InputFormat permits BedInputFormat, TsvInputFormat {
  AnnotationType annotationType();
}
