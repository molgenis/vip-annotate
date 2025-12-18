package org.molgenis.vipannotate.annotation.spec;

import static org.molgenis.vipannotate.annotation.spec.ScalarType.Category.FLOATING_POINT;
import static org.molgenis.vipannotate.annotation.spec.ScalarType.Category.INTEGER;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ScalarType {
  /** 8-bit signed integer */
  @JsonProperty("i8")
  I8(INTEGER, 8),
  /** 16-bit signed integer */
  @JsonProperty("i16")
  I16(INTEGER, 16),
  /** 32-bit signed integer */
  @JsonProperty("i32")
  I32(INTEGER, 32),
  /** 64-bit signed integer */
  @JsonProperty("i64")
  I64(INTEGER, 64),
  /** 8-bit unsigned integer */
  @JsonProperty("u8")
  U8(INTEGER, 8),
  /** 16-bit unsigned integer */
  @JsonProperty("u16")
  U16(INTEGER, 16),
  /** 32-bit unsigned integer */
  @JsonProperty("u32")
  U32(INTEGER, 32),
  /** 64-bit unsigned integer */
  @JsonProperty("u64")
  U64(INTEGER, 64),
  /** 32-bit floating point number */
  @JsonProperty("f32")
  F32(FLOATING_POINT, 32),
  /** 64-bit floating point number */
  @JsonProperty("f64")
  F64(FLOATING_POINT, 64);

  public enum Category {
    INTEGER,
    FLOATING_POINT
  }

  @Getter private final Category category;
  @Getter private final int bitSize;

  public int getByteSize() {
    return bitSize / 8;
  }
}
