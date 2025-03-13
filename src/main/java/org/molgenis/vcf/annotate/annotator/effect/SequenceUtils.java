package org.molgenis.vcf.annotate.annotator.effect;

import org.molgenis.vcf.annotate.db.effect.model.Strand;

public class SequenceUtils {
  private SequenceUtils() {}

  public static byte getComplementaryBase(byte refBase) {
    return switch (refBase) {
      case 'A' -> 'T';
      case 'C' -> 'G';
      case 'G' -> 'C';
      case 'T' -> 'A';
      case 'N' -> 'N';
      default -> throw new IllegalArgumentException();
    };
  }

  public static char getBase(byte[] bases, Strand strand) {
    return (char)
        switch (strand) {
          case POSITIVE -> bases[0];
          case NEGATIVE -> getComplementaryBase(bases[0]);
        };
  }
}
