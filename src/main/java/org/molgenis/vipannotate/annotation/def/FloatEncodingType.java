package org.molgenis.vipannotate.annotation.def;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FloatEncodingType {
  @JsonProperty("lossless")
  LOSSLESS,
  @JsonProperty("lossy_q8")
  Q8,
  @JsonProperty("lossy_q16")
  Q16
}
