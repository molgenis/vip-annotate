package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.util.Numbers.*;

import java.util.EnumSet;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.AlleleUtils;

public record GnomAdTsvRecord(
    String chrom,
    int pos, // 1-based
    String ref,
    String alt,
    @Nullable Double afExomes,
    @Nullable Double afGenomes,
    @Nullable Double afJoint,
    @Nullable Double faf95Exomes,
    @Nullable Double faf95Genomes,
    @Nullable Double faf95Joint,
    @Nullable Double faf99Exomes,
    @Nullable Double faf99Genomes,
    @Nullable Double faf99Joint,
    @Nullable Integer nhomaltExomes,
    @Nullable Integer nhomaltGenomes,
    @Nullable Integer nhomaltJoint,
    EnumSet<Filter> exomesFilters,
    EnumSet<Filter> genomesFilters,
    boolean notCalledInExomes,
    boolean notCalledInGenomes,
    @Nullable Double covExomes,
    @Nullable Double covGenomes,
    @Nullable Double covJoint) {

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
    if (!AlleleUtils.isActg(allele)) {
      throw new IllegalArgumentException(allele + " is not a valid allele.");
    }
  }
}
