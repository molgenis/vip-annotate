package org.molgenis.vipannotate.annotation.resolved;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** integer type */
@RequiredArgsConstructor
public enum IntType implements ScalarType {
  /** 8-bit signed integer */
  @JsonProperty("i8")
  I8(8),
  /** 16-bit signed integer */
  @JsonProperty("i16")
  I16(16),
  /** 32-bit signed integer */
  @JsonProperty("i32")
  I32(32),
  /** 64-bit signed integer */
  @JsonProperty("i64")
  I64(64),
  /** 8-bit unsigned integer */
  @JsonProperty("u8")
  U8(8),
  /** 16-bit unsigned integer */
  @JsonProperty("u16")
  U16(16),
  /** 32-bit unsigned integer */
  @JsonProperty("u32")
  U32(32),
  /** 64-bit unsigned integer */
  @JsonProperty("u64")
  U64(64);

  @Getter private final int bitSize;
}
