package org.molgenis.vipannotate.annotation.resolved;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** integer type */
@RequiredArgsConstructor
public enum IntType implements ScalarType {
  /** 8-bit signed integer */
  I8(8),
  /** 16-bit signed integer */
  I16(16),
  /** 32-bit signed integer */
  I32(32),
  /** 64-bit signed integer */
  I64(64),
  /** 8-bit unsigned integer */
  U8(8),
  /** 16-bit unsigned integer */
  U16(16),
  /** 32-bit unsigned integer */
  U32(32),
  /** 64-bit unsigned integer */
  U64(64);

  @Getter private final int bitSize;
}
