package org.molgenis.vipannotate.format.vdb;

import lombok.Getter;

public enum Compression {
  ZSTD(0),
  PLAIN(1);

  @Getter private final int value;

  Compression(int value) {
    this.value = value;
  }

  public static Compression fromValue(int value) {
    return switch (value) {
      case 0 -> ZSTD;
      case 1 -> PLAIN;
      default -> throw new IllegalStateException("unexpected value: %d".formatted(value));
    };
  }
}
