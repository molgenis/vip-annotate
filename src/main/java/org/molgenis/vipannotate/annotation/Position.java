package org.molgenis.vipannotate.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Genomic position */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Position extends Interval {

  public Position(@NonNull Contig contig, int pos) {
    super(contig, pos, pos);
  }
}
