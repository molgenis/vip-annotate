package org.molgenis.vipannotate.annotator.effect;

import org.molgenis.vipannotate.db.effect.model.FuryFactory;

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

  public static char getBase(byte[] bases, FuryFactory.Strand strand) {
    return (char)
        switch (strand) {
          case POSITIVE -> bases[0];
          case NEGATIVE -> getComplementaryBase(bases[0]);
        };
  }
}
