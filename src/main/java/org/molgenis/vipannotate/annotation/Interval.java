package org.molgenis.vipannotate.annotation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Genomic interval [start, stop] */
@Getter
@ToString
@EqualsAndHashCode
public class Interval implements Feature {
  /** genome contig identifier */
  private Contig contig;

  /** genome start position (inclusive, 1-based) */
  private int start;

  /** genome stop position (inclusive, 1-based) */
  private int stop;

  // TODO validate start-stop against contig.length is not null
  public Interval(Contig contig, int start, int stop) {
    validate(start, stop);
    this.contig = contig;
    this.start = start;
    this.stop = stop;
  }

  public void reset(Contig contig, int start, int stop) {
    validate(start, stop);
    this.contig = contig;
    this.start = start;
    this.stop = stop;
  }

  private static void validate(int start, int stop) {
    if (start < 0 || stop < start) {
      throw new IllegalArgumentException("invalid genomic interval");
    }
  }
}
