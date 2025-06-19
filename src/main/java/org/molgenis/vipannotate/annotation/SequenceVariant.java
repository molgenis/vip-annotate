package org.molgenis.vipannotate.annotation;

import lombok.*;

/** genomic sequence variant */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SequenceVariant extends Interval {
  private final byte[] alt;

  public SequenceVariant(@NonNull Contig contig, int start, int stop, byte @NonNull [] alt) {
    super(contig, start, stop);
    this.alt = alt;
  }

  /**
   * @return number of reference allele bases
   */
  public int getRefLength() {
    return getStop() - getStart() + 1;
  }

  /**
   * @return number of alternate allele bases
   */
  public int getAltLength() {
    return alt.length;
  }
}
