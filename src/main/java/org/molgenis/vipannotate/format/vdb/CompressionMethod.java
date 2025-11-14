package org.molgenis.vipannotate.format.vdb;

import lombok.Getter;

public enum CompressionMethod {
  PLAIN(0),
  ZSTD(1);

  @Getter private final int value;

  CompressionMethod(int value) {
    this.value = value;
  }

  public static CompressionMethod fromValue(int value) {
    return switch (value) {
      case 0 -> PLAIN;
      case 1 -> ZSTD;
      default -> throw new IllegalStateException("unexpected value: %d".formatted(value));
    };
  }
}
