package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AnnotationType {
  @JsonProperty("sequence_variant")
  SEQUENCE_VARIANT,
  @JsonProperty("position")
  POSITION
}
