package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CoordinateSystem {
  @JsonProperty("0-based")
  ZERO_BASED,

  @JsonProperty("1-based")
  ONE_BASED
}
