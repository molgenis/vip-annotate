package org.molgenis.vipannotate.annotation.gnomad;

import static org.molgenis.vipannotate.util.Numbers.*;

import java.util.EnumSet;
import lombok.NonNull;
import org.molgenis.vipannotate.annotation.Annotation;

public record GnomAdAnnotation(
    @NonNull Source source,
    Double af,
    double faf95,
    double faf99,
    int hn,
    @NonNull EnumSet<Filter> filters,
    double cov)
    implements Annotation {
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

  public GnomAdAnnotation {
    validateNonNegativeOrNull(af);
    validateNonNegative(faf95);
    validateNonNegative(faf99);
    validateNonNegative(hn);
    validateNonNegative(cov);
  }
}
