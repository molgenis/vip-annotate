package org.molgenis.vipannotate.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** A sequence variant in the context of a gene */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SequenceVariantGeneContext extends SequenceVariant {
  private final Gene gene;

  public SequenceVariantGeneContext(
      Contig contig, int start, int stop, AltAllele alt, SequenceVariantType type, Gene gene) {
    super(contig, start, stop, alt, type);
    this.gene = gene;
  }
}
