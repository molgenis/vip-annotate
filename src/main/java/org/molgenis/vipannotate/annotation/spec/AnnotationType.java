package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AnnotationType {
  @JsonProperty("interval")
  INTERVAL,
  @JsonProperty("position")
  POSITION,
  @JsonProperty("sequence_variant")
  SEQUENCE_VARIANT
}
