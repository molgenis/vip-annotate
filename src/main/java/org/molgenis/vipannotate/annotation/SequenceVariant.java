package org.molgenis.vipannotate.annotation;

import lombok.*;
import org.jspecify.annotations.Nullable;

/**
 * A difference between a reference sequence and an observed sequence.
 *
 * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001060">SO:0001060</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SequenceVariant extends Interval {
  @Nullable private final String alt;
  private final SequenceVariantType type;

  public SequenceVariant(Contig contig, int start, int stop, String alt, SequenceVariantType type) {
    super(contig, start, stop);
    this.alt = alt;
    this.type = type;
  }

  public static SequenceVariantType fromVcfString(int refLength, String alt) {
    SequenceVariantType sequenceVariantType;
    // null - missing value ('.' in vcf)
    if (alt == null) {
      sequenceVariantType = SequenceVariantType.OTHER;
    }
    // * allele missing due to overlapping deletion
    else if (alt.length() == 1 && alt.charAt(0) == '*') {
      sequenceVariantType = SequenceVariantType.OTHER;
    }
    // <*> the unspecified allele
    else if (alt.length() == 3
        && alt.charAt(1) == '*'
        && alt.charAt(0) == '<'
        && alt.charAt(2) == '>') {
      sequenceVariantType = SequenceVariantType.OTHER;
    }
    // symbolic allele such as <CNV>, <CNV:TR>, <DEL>, <DUP> or
    else if (alt.length() > 2 && alt.charAt(0) == '<' && alt.charAt(alt.length() - 1) == '>') {
      sequenceVariantType = SequenceVariantType.STRUCTURAL;
    } else if (isComplexRearrangement(alt)) {
      sequenceVariantType = SequenceVariantType.STRUCTURAL;
    } else {
      // now can we assume that alt only has ACTGN nucleotides
      if (refLength == 1 && alt.length() == 1) {
        sequenceVariantType = SequenceVariantType.SNV;
      } else if (refLength == alt.length()) {
        sequenceVariantType = SequenceVariantType.MNV;
      } else if (refLength == 1 && alt.length() > 1) {
        sequenceVariantType = SequenceVariantType.INSERTION;
      } else if (alt.length() == 1 && refLength > 1) {
        sequenceVariantType = SequenceVariantType.DELETION;
      } else {
        sequenceVariantType = SequenceVariantType.INDEL;
      }
    }
    return sequenceVariantType;
  }

  private static boolean isComplexRearrangement(String alt) {
    boolean isComplexRearrangement = false;
    for (int i = 0, altLength = alt.length(); i < altLength; i++) {
      char c = alt.charAt(i);
      if (c == '[' || c == ']') {
        isComplexRearrangement = true;
        break;
      }
    }
    return isComplexRearrangement;
  }

  /**
   * @return number of reference allele bases
   */
  public int getRefLength() {
    return getStop() - getStart() + 1;
  }
}
