package org.molgenis.vcf.annotate.db2.gnomad;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.*;
import org.molgenis.vcf.annotate.util.Quantized16UnitIntervalDouble;

/** V5, but with separate exomes/genomes data */
@Value
@EqualsAndHashCode
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GnomAdShortVariantAnnotation implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  VariantData exomes;
  VariantData genomes;
  VariantData joint;

  @Value
  @EqualsAndHashCode
  @Builder
  @AllArgsConstructor(access = AccessLevel.PUBLIC)
  public static class VariantData implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    short quantizedAf; // TODO move quantized stuff to encoder
    short quantizedFaf95;
    short quantizedFaf99;
    short quantizedCov;
    int nHomAlt; // -1 for null
    FilterV2 filters; // TODO with our codec, we can make this EnumSet<FilterV2>

    public Double getAf() {
      return Quantized16UnitIntervalDouble.toDouble(quantizedAf);
    }

    public Double getFaf95() {
      return Quantized16UnitIntervalDouble.toDouble(quantizedFaf95);
    }

    public Double getFaf99() {
      return Quantized16UnitIntervalDouble.toDouble(quantizedFaf99);
    }

    public Double getCov() {
      return Quantized16UnitIntervalDouble.toDouble(quantizedFaf99);
    }

    public Integer getHomAlt() {
      return nHomAlt != -1 ? nHomAlt : null;
    }
  }

  @Getter
  public enum FilterV2 {
    AC0("AC0"),
    AC0_AND_AS_VQSR("AC0,AS_VQSR"),
    AC0_AND_INBREEDING_COEFF("AC0,InbreedingCoeff"),
    AC0_AND_AS_VQSR_AND_INBREEDING_COEFF("AC0,AS_VQSR,InbreedingCoeff"),
    AS_VQSR("AS_VQSR"),
    AS_VQSR_AND_INBREEDING_COEFF("AS_VQSR,InbreedingCoeff"),
    INBREEDING_COEFF("InbreedingCoeff");

    private static final Map<String, FilterV2> ID_TO_FILTER_MAP;

    static {
      ID_TO_FILTER_MAP = HashMap.newHashMap(FilterV2.values().length);
      for (FilterV2 filter : FilterV2.values()) {
        ID_TO_FILTER_MAP.put(filter.getId(), filter);
      }
    }

    private final String id;

    FilterV2(String id) {
      this.id = requireNonNull(id);
    }

    public static FilterV2 from(String id) {
      return ID_TO_FILTER_MAP.get(requireNonNull(id));
    }
  }
}
