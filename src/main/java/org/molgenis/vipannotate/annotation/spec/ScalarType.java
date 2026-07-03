package org.molgenis.vipannotate.annotation.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ScalarType {
  /** 8-bit signed integer */
  @JsonProperty("i8")
  I8,
  /** 16-bit signed integer */
  @JsonProperty("i16")
  I16,
  /** 32-bit signed integer */
  @JsonProperty("i32")
  I32,
  /** 64-bit signed integer */
  @JsonProperty("i64")
  I64,
  /** 8-bit unsigned integer */
  @JsonProperty("u8")
  U8,
  /** 16-bit unsigned integer */
  @JsonProperty("u16")
  U16,
  /** 32-bit unsigned integer */
  @JsonProperty("u32")
  U32,
  /** 64-bit unsigned integer */
  @JsonProperty("u64")
  U64,
  /** 32-bit floating point number */
  @JsonProperty("f32")
  F32,
  /** 64-bit floating point number */
  @JsonProperty("f64")
  F64;
}
