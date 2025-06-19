package org.molgenis.vipannotate.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/** Genomic interval [start, stop] */
@Getter
@ToString
@EqualsAndHashCode
public class Interval implements Feature {
  /** genome contig identifier */
  private final Contig contig;

  /** genome start position (inclusive, 1-based) */
  private final int start;

  /** genome stop position (inclusive, 1-based) */
  private final int stop;

  public Interval(@NonNull Contig contig, int start, int stop) {
    if (start < 0 || stop < 0 || stop < start) {
      throw new IllegalArgumentException("invalid genomic interval");
    }
    this.contig = contig;
    this.start = start;
    this.stop = stop;
  }
}
