package org.molgenis.vipannotate.annotation.phylop;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;

/**
 * @param chr chromosome
 * @param start start position (0-based, inclusive)
 * @param end end position (0-based, exclusive)
 * @param score conservation score
 */
public record PhyloPBedFeature(@NonNull String chr, int start, int end, double score) {
  public PhyloPBedFeature {
    validateNonNegative(start);
    if (start != end - 1) {
      throw new IllegalArgumentException("end value must be start value plus one");
    }
  }
}
