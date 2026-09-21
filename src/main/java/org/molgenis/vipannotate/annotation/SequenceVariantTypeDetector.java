package org.molgenis.vipannotate.annotation;

import org.molgenis.vipannotate.format.vcf.AltAllele;

public class SequenceVariantTypeDetector {
  private SequenceVariantTypeDetector() {}

  public static SequenceVariantType determineType(int refLength, AltAllele altAllele) {
    return switch (altAllele.getType()) {
      case BASES -> {
        int altLength = altAllele.get().length();
        if (refLength == 1 && altLength == 1) {
          yield SequenceVariantType.SNV;
        } else if (refLength == altLength) {
          yield SequenceVariantType.MNV;
        } else if (refLength == 1 && altLength > 1) {
          yield SequenceVariantType.INSERTION;
        } else if (altLength == 1 && refLength > 1) {
          yield SequenceVariantType.DELETION;
        } else {
          yield SequenceVariantType.INDEL;
        }
      }
      case BREAKEND_REPLACEMENT, SINGLE_BREAKEND, SYMBOLIC -> SequenceVariantType.STRUCTURAL;
      case MISSING, MISSING_OVERLAPPING_DELETION, UNSPECIFIED -> SequenceVariantType.OTHER;
    };
  }
}
