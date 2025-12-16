package org.molgenis.vipannotate.format.vdb;

import lombok.Getter;

public enum IoMode {
  /** aligned reading using direct IO */
  DIRECT(0),
  /** unaligned reading using buffered IO */
  BUFFERED(1);

  @Getter private final int value;

  IoMode(int value) {
    this.value = value;
  }

  public static IoMode fromValue(int value) {
    return switch (value) {
      case 0 -> DIRECT;
      case 1 -> BUFFERED;
      default -> throw new IllegalStateException("unexpected value: %d".formatted(value));
    };
  }
}
