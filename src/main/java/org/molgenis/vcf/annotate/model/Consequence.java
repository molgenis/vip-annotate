package org.molgenis.vcf.annotate.model;

import static java.util.Objects.requireNonNull;

import lombok.Getter;

@Getter
public enum Consequence {
  STOP_GAINED("stop_gained", Impact.HIGH),
  START_LOST("start_lost", Impact.HIGH),
  STOP_LOST("stop_lost", Impact.HIGH),
  MISSENSE_VARIANT("missense_variant", Impact.MODERATE),
  STOP_RETAINED_VARIANT("stop_retained_variant", Impact.LOW),
  SYNONYMOUS_VARIANT("synonymous_variant", Impact.LOW),
  INTRON_VARIANT("intron_variant", Impact.MODIFIER),
  INTERGENIC_VARIANT("intergenic_variant", Impact.MODIFIER),
  UTR_VARIANT("UTR_variant", Impact.MODIFIER),
  THREE_PRIME_UTR_VARIANT("3_prime_UTR_variant", Impact.MODIFIER),
  FIVE_PRIME_UTR_VARIANT("5_prime_UTR_variant", Impact.MODIFIER);

  private final String term;
  private final Impact impact;

  Consequence(String term, Impact impact) {
    this.term = requireNonNull(term);
    this.impact = requireNonNull(impact);
  }
}
