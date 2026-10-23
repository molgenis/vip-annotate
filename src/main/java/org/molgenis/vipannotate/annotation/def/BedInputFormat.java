package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Map;

@JsonTypeName("bed")
public record BedInputFormat(
    @JsonProperty(value = "annotations", required = true) Map<String, BedFieldType> annotations)
    implements InputFormat {
  @Override
  public AnnotationType annotationType() {
    return AnnotationType.INTERVAL;
  }
}
