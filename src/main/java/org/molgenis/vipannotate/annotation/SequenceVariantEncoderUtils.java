package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.util.AlleleUtils;

public class SequenceVariantEncoderUtils {
  private SequenceVariantEncoderUtils() {}

  public static EncodedSequenceVariant.Type determineType(SequenceVariant variant) {
    boolean isSmall =
        switch (variant.getType()) {
          case SNV -> {
            CharSequence alt = variant.getAlt().get();
            yield AlleleUtils.isActg(alt);
          }
          case MNV, INDEL, INSERTION, DELETION -> {
            CharSequence alt = variant.getAlt().get();
            yield alt.length() <= 4 && AlleleUtils.isActg(alt) && variant.getRefLength() <= 16;
          }
          case STRUCTURAL, OTHER -> false;
        };
    return isSmall ? EncodedSequenceVariant.Type.SMALL : EncodedSequenceVariant.Type.BIG;
  }

  /**
   * Encode position as int
   *
   * @param pos position >= 0
   * @return position encoded in 18 bits
   */
  public static int encodePos(int pos) {
    if (pos < 1) throw new IllegalArgumentException("start must be greater than or equal to 0");
    return Partition.calcPosInBin(pos); // TODO this should not happen here
  }

  /** encodes number of bases as zero-based number */
  public static int encodeNrBases(int nrBases) {
    return nrBases - 1;
  }

  static int encodeAltBase(char altBase) {
    return switch (altBase) {
      case 'A' -> 0;
      case 'C' -> 1;
      case 'G' -> 2;
      case 'T' -> 3;
      default ->
          throw new IllegalArgumentException(
              "alt base '%s' not allowed, must be one of A,C,G,T".formatted(altBase));
    };
  }
}
