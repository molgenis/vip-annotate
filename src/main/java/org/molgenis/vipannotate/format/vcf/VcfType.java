package org.molgenis.vipannotate.format.vcf;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public enum VcfType {
  UNCOMPRESSED(-1),
  COMPRESSED(),
  COMPRESSED_LVL0(0),
  COMPRESSED_LVL1(1),
  COMPRESSED_LVL2(2),
  COMPRESSED_LVL3(3),
  COMPRESSED_LVL4(4),
  COMPRESSED_LVL5(5),
  COMPRESSED_LVL6(6),
  COMPRESSED_LVL7(7),
  COMPRESSED_LVL8(8),
  COMPRESSED_LVL9(9);

  @Nullable private final Integer compressionLevel;

  VcfType() {
    this(null);
  }

  VcfType(@Nullable Integer compressionLevel) {
    if (compressionLevel != null && (compressionLevel < -1 || compressionLevel > 9)) {
      throw new IllegalArgumentException("Compression level must be between -1 and 9");
    }
    this.compressionLevel = compressionLevel;
  }

  public static VcfType fromCompressionLevel(Integer compressionLevel) {
    return switch (compressionLevel) {
      case 0 -> COMPRESSED_LVL0;
      case 1 -> COMPRESSED_LVL1;
      case 2 -> COMPRESSED_LVL2;
      case 3 -> COMPRESSED_LVL3;
      case 4 -> COMPRESSED_LVL4;
      case 5 -> COMPRESSED_LVL5;
      case 6 -> COMPRESSED_LVL6;
      case 7 -> COMPRESSED_LVL7;
      case 8 -> COMPRESSED_LVL8;
      case 9 -> COMPRESSED_LVL9;
      default ->
          throw new IllegalArgumentException(
              "Unknown compression level: %d".formatted(compressionLevel));
    };
  }
}
