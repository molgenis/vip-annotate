package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public class AltAlleleRegistry {
  private static final AltAllele[] ALT_ALLELES;

  static {
    ALT_ALLELES = new AltAllele[21];
    ALT_ALLELES[0] = new AltAllele("A");
    ALT_ALLELES[1] = new AltAllele("C");
    ALT_ALLELES[2] = new AltAllele("G");
    ALT_ALLELES[3] = new AltAllele("T");
    ALT_ALLELES[4] = new AltAllele("N");
    ALT_ALLELES[5] = new AltAllele("AA");
    ALT_ALLELES[6] = new AltAllele("AC");
    ALT_ALLELES[7] = new AltAllele("AG");
    ALT_ALLELES[8] = new AltAllele("AT");
    ALT_ALLELES[9] = new AltAllele("CA");
    ALT_ALLELES[10] = new AltAllele("CC");
    ALT_ALLELES[11] = new AltAllele("CG");
    ALT_ALLELES[12] = new AltAllele("CT");
    ALT_ALLELES[13] = new AltAllele("GA");
    ALT_ALLELES[14] = new AltAllele("GC");
    ALT_ALLELES[15] = new AltAllele("GG");
    ALT_ALLELES[16] = new AltAllele("GT");
    ALT_ALLELES[17] = new AltAllele("TA");
    ALT_ALLELES[18] = new AltAllele("TC");
    ALT_ALLELES[19] = new AltAllele("TG");
    ALT_ALLELES[20] = new AltAllele("TT");
  }

  public static AltAllele get(@Nullable String alt) {
    AltAllele altAllele;
    if (alt != null) {
      altAllele =
          switch (alt) {
            case "A" -> ALT_ALLELES[0];
            case "C" -> ALT_ALLELES[1];
            case "G" -> ALT_ALLELES[2];
            case "T" -> ALT_ALLELES[3];
            case "N" -> ALT_ALLELES[4];
            case "AA" -> ALT_ALLELES[5];
            case "AC" -> ALT_ALLELES[6];
            case "AG" -> ALT_ALLELES[7];
            case "AT" -> ALT_ALLELES[8];
            case "CA" -> ALT_ALLELES[9];
            case "CC" -> ALT_ALLELES[10];
            case "CG" -> ALT_ALLELES[11];
            case "CT" -> ALT_ALLELES[12];
            case "GA" -> ALT_ALLELES[13];
            case "GC" -> ALT_ALLELES[14];
            case "GG" -> ALT_ALLELES[15];
            case "GT" -> ALT_ALLELES[16];
            case "TA" -> ALT_ALLELES[17];
            case "TC" -> ALT_ALLELES[18];
            case "TG" -> ALT_ALLELES[19];
            case "TT" -> ALT_ALLELES[20];
            default -> new AltAllele(alt);
          };
    } else {
      altAllele = new AltAllele(null);
    }
    return altAllele;
  }
}
