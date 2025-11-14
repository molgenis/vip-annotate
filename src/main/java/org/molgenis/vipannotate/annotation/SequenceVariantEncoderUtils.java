package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.AlleleUtils;
import org.molgenis.vipannotate.util.Numbers;

public class SequenceVariantEncoderUtils {
  private SequenceVariantEncoderUtils() {}

  public static EncodedSequenceVariant.Type determineType(SequenceVariant variant) {
    return switch (variant.getType()) {
      case SNV -> {
        CharSequence alt = variant.getAlt().get();
        yield AlleleUtils.isActg(alt)
            ? EncodedSequenceVariant.Type.SMALL
            : EncodedSequenceVariant.Type.OTHER;
      }
      case MNV, INDEL, INSERTION, DELETION -> {
        CharSequence alt = variant.getAlt().get();
        if (!AlleleUtils.isActg(alt)) {
          yield EncodedSequenceVariant.Type.OTHER;
        }
        // TODO thresholds assume that partition size is static globally
        yield alt.length() <= 4 && variant.getRefLength() <= 16
            ? EncodedSequenceVariant.Type.SMALL
            : EncodedSequenceVariant.Type.BIG;
      }
      case STRUCTURAL, OTHER -> EncodedSequenceVariant.Type.OTHER;
    };
  }

  /**
   * Encode one-based position as int. Position zero indicates telomere.
   *
   * @param pos position >= 0
   * @return position encoded in 18 bits
   */
  public static int encodePos(int pos) {
    Numbers.validateNonNegative(pos);
    // TODO encoding assume that partition size is static globally
    return Partition.calcPosInBin(pos);
  }

  /** encodes positive one-based number of bases as zero-based number */
  public static int encodeBaseCount(int nrBases) {
    Numbers.validatePositive(nrBases);
    return nrBases - 1;
  }

  /** Encodes A,C,G,T (case-insensitive) base in two bits. */
  static int encodeActgBase(char base) {
    return switch (base) {
      case 'A', 'a' -> 0b00;
      case 'C', 'c' -> 0b01;
      case 'G', 'g' -> 0b10;
      case 'T', 't' -> 0b11;
      default ->
          throw new IllegalArgumentException(
              "base '%s' not allowed, must be one of A,C,G,T".formatted(base));
    };
  }

  /** Encodes A,C,G,T,N (case-insensitive) base in three bits. */
  static int encodeActgnBase(char base) {
    return switch (base) {
      case 'A', 'a' -> 0b000;
      case 'C', 'c' -> 0b001;
      case 'G', 'g' -> 0b010;
      case 'T', 't' -> 0b011;
      case 'N', 'n' -> 0b100;
      default ->
          throw new IllegalArgumentException(
              "base '%s' not allowed, must be one of A,C,G,T,N".formatted(base));
    };
  }
}
