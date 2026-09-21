package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** <a href="https://en.wikipedia.org/wiki/IEEE_754">IEEE 754-2019</a> floating point formats */
@RequiredArgsConstructor
public enum FloatType implements ScalarType {
  /** IEEE 754-2019 binary32 */
  @JsonProperty("f32")
  F32(32),
  /** IEEE 754-2019 binary64 */
  @JsonProperty("f64")
  F64(64);

  @Getter private final int bitSize;
}
