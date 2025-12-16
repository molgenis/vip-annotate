package org.molgenis.vipannotate.annotation.fathmmmkl;

import static org.molgenis.vipannotate.util.Numbers.*;

import org.molgenis.vipannotate.util.AlleleUtils;

public record FathmmMklTsvRecord(
    String chrom,
    int pos, // 1-based
    String ref,
    String alt,
    double score) {

  public FathmmMklTsvRecord {
    validatePositive(pos);
    validateAllele(ref);
    validateAllele(alt);
  }

  private void validateAllele(String allele) {
    if (!AlleleUtils.isActg(allele)) {
      throw new IllegalArgumentException(allele + " is not a valid allele.");
    }
  }
}
