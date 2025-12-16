package org.molgenis.vipannotate.format.vcf;

import org.jspecify.annotations.Nullable;

/** Registry of common alternate alleles to reduce pressure on the garbage collector */
public enum AltAlleleRegistry {
  INSTANCE;

  private static final int INDEX_A = 0;
  private static final int INDEX_C = 1;
  private static final int INDEX_G = 2;
  private static final int INDEX_T = 3;
  private static final int INDEX_AA = 4;
  private static final int INDEX_AC = 5;
  private static final int INDEX_AG = 6;
  private static final int INDEX_AT = 7;
  private static final int INDEX_CA = 8;
  private static final int INDEX_CC = 9;
  private static final int INDEX_CG = 10;
  private static final int INDEX_CT = 11;
  private static final int INDEX_GA = 12;
  private static final int INDEX_GC = 13;
  private static final int INDEX_GG = 14;
  private static final int INDEX_GT = 15;
  private static final int INDEX_TA = 16;
  private static final int INDEX_TC = 17;
  private static final int INDEX_TG = 18;
  private static final int INDEX_TT = 19;

  private static final AltAllele[] ALT_ALLELES;

  static {
    ALT_ALLELES = new AltAllele[20];
    ALT_ALLELES[INDEX_A] = new AltAllele("A");
    ALT_ALLELES[INDEX_C] = new AltAllele("C");
    ALT_ALLELES[INDEX_G] = new AltAllele("G");
    ALT_ALLELES[INDEX_T] = new AltAllele("T");
    ALT_ALLELES[INDEX_AA] = new AltAllele("AA");
    ALT_ALLELES[INDEX_AC] = new AltAllele("AC");
    ALT_ALLELES[INDEX_AG] = new AltAllele("AG");
    ALT_ALLELES[INDEX_AT] = new AltAllele("AT");
    ALT_ALLELES[INDEX_CA] = new AltAllele("CA");
    ALT_ALLELES[INDEX_CC] = new AltAllele("CC");
    ALT_ALLELES[INDEX_CG] = new AltAllele("CG");
    ALT_ALLELES[INDEX_CT] = new AltAllele("CT");
    ALT_ALLELES[INDEX_GA] = new AltAllele("GA");
    ALT_ALLELES[INDEX_GC] = new AltAllele("GC");
    ALT_ALLELES[INDEX_GG] = new AltAllele("GG");
    ALT_ALLELES[INDEX_GT] = new AltAllele("GT");
    ALT_ALLELES[INDEX_TA] = new AltAllele("TA");
    ALT_ALLELES[INDEX_TC] = new AltAllele("TC");
    ALT_ALLELES[INDEX_TG] = new AltAllele("TG");
    ALT_ALLELES[INDEX_TT] = new AltAllele("TT");
  }

  public AltAllele get(CharSequence charSequence) {
    AltAllele altAllele =
        switch (charSequence.length()) {
          case 1 -> get(charSequence.charAt(0));
          case 2 -> get(charSequence.charAt(0), charSequence.charAt(1));
          default -> null;
        };
    return altAllele != null ? altAllele : new AltAllele(charSequence);
  }

  private @Nullable AltAllele get(char c) {
    return switch (c) {
      case 'A' -> ALT_ALLELES[INDEX_A];
      case 'C' -> ALT_ALLELES[INDEX_C];
      case 'G' -> ALT_ALLELES[INDEX_G];
      case 'T' -> ALT_ALLELES[INDEX_T];
      default -> null;
    };
  }

  private @Nullable AltAllele get(char c0, char c1) {
    return switch (c0) {
      case 'A' ->
          switch (c1) {
            case 'A' -> ALT_ALLELES[INDEX_AA];
            case 'C' -> ALT_ALLELES[INDEX_AC];
            case 'G' -> ALT_ALLELES[INDEX_AG];
            case 'T' -> ALT_ALLELES[INDEX_AT];
            default -> null;
          };
      case 'C' ->
          switch (c1) {
            case 'A' -> ALT_ALLELES[INDEX_CA];
            case 'C' -> ALT_ALLELES[INDEX_CC];
            case 'G' -> ALT_ALLELES[INDEX_CG];
            case 'T' -> ALT_ALLELES[INDEX_CT];
            default -> null;
          };
      case 'G' ->
          switch (c1) {
            case 'A' -> ALT_ALLELES[INDEX_GA];
            case 'C' -> ALT_ALLELES[INDEX_GC];
            case 'G' -> ALT_ALLELES[INDEX_GG];
            case 'T' -> ALT_ALLELES[INDEX_GT];
            default -> null;
          };
      case 'T' ->
          switch (c1) {
            case 'A' -> ALT_ALLELES[INDEX_TA];
            case 'C' -> ALT_ALLELES[INDEX_TC];
            case 'G' -> ALT_ALLELES[INDEX_TG];
            case 'T' -> ALT_ALLELES[INDEX_TT];
            default -> null;
          };
      default -> null;
    };
  }
}
