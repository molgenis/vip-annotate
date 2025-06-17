package org.molgenis.vipannotate.annotation.phylop;

import lombok.NonNull;
import org.molgenis.vipannotate.util.Numbers;

/**
 * @param chr chromosome
 * @param start start position (0-based, inclusive)
 * @param end end position (0-based, exclusive)
 * @param score conservation score
 */
public record PhyloPBedFeature(@NonNull String chr, int start, int end, double score) {
  public PhyloPBedFeature {
    Numbers.requireNonNegative(start);
    if (start != end - 1) {
      throw new IllegalArgumentException("end value must be start value plus one");
    }
  }
}
