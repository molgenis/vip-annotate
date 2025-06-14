package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.util.ParameterValidation.*;

import java.util.EnumSet;
import lombok.NonNull;

public record GnomAdAnnotationData(
    @NonNull Source source,
    Double af,
    double faf95,
    double faf99,
    int hn,
    @NonNull EnumSet<Filter> filters,
    double cov) {
  public enum Source {
    GENOMES,
    EXOMES,
    TOTAL
  }

  public enum Filter {
    AC0,
    AS_VQSR,
    INBREEDING_COEFF
  }

  public GnomAdAnnotationData {
    validateNonNegativeOrNull(af);
    validateNonNegative(faf95);
    validateNonNegative(faf99);
    validateNonNegative(hn);
    validateNonNegative(cov);
  }
}
