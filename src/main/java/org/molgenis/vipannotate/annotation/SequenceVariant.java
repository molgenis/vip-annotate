package org.molgenis.vipannotate.annotation;

import lombok.*;
import org.molgenis.vipannotate.format.vcf.AltAllele;

/**
 * A difference between a reference sequence and an observed sequence.
 *
 * @see <a href="http://sequenceontology.org/browser/release_2.5.3/term/SO:0001060">SO:0001060</a>
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SequenceVariant extends Interval {
  private final AltAllele alt;
  private final SequenceVariantType type;

  public SequenceVariant(
      Contig contig, int start, int stop, AltAllele alt, SequenceVariantType type) {
    super(contig, start, stop);
    this.alt = alt;
    this.type = type;
  }

  /** {@return number of reference allele bases} */
  public int getRefLength() {
    return getStop() - getStart() + 1;
  }
}
