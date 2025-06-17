package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.util.Numbers.*;

import java.util.EnumSet;
import lombok.NonNull;

public record GnomAdTsvRecord(
    @NonNull String chrom,
    int pos,
    @NonNull String ref,
    @NonNull String alt,
    Double afExomes,
    Double afGenomes,
    Double afJoint,
    Double faf95Exomes,
    Double faf95Genomes,
    Double faf95Joint,
    Double faf99Exomes,
    Double faf99Genomes,
    Double faf99Joint,
    Integer nhomaltExomes,
    Integer nhomaltGenomes,
    Integer nhomaltJoint,
    @NonNull EnumSet<Filter> exomesFilters,
    @NonNull EnumSet<Filter> genomesFilters,
    boolean notCalledInExomes,
    boolean notCalledInGenomes,
    Double covExomes,
    Double covGenomes,
    Double covJoint) {

  public enum Filter {
    AC0,
    AS_VQSR,
    INBREEDING_COEFF
  }

  public GnomAdTsvRecord {
    validatePositive(pos);
    validateAllele(ref);
    validateAllele(alt);
    validateNonNegativeOrNull(afExomes);
    validateNonNegativeOrNull(afGenomes);
    validateNonNegativeOrNull(afJoint);
    validateNonNegativeOrNull(faf95Exomes);
    validateNonNegativeOrNull(faf95Genomes);
    validateNonNegativeOrNull(faf95Joint);
    validateNonNegativeOrNull(faf99Exomes);
    validateNonNegativeOrNull(faf99Genomes);
    validateNonNegativeOrNull(faf99Joint);
    validateNonNegativeOrNull(nhomaltExomes);
    validateNonNegativeOrNull(nhomaltGenomes);
    validateNonNegativeOrNull(nhomaltJoint);
    validateNonNegativeOrNull(covExomes);
    validateNonNegativeOrNull(covGenomes);
    validateNonNegativeOrNull(covJoint);
    if (notCalledInExomes && notCalledInGenomes) {
      throw new IllegalArgumentException("notCalledInExomes or notCalledInGenomes must be true");
    }
  }

  private void validateAllele(String allele) {
    for (int i = 0, alleleLength = allele.length(); i < alleleLength; i++) {
      switch (allele.charAt(i)) {
        case 'A':
        case 'C':
        case 'T':
        case 'G':
          break;
        default:
          throw new IllegalArgumentException(allele + " is not a valid allele.");
      }
    }
  }
}
