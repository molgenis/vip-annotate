package org.molgenis.vipannotate.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.Feature;

/** Genomic interval [start, stop] */
@Getter
@ToString
@EqualsAndHashCode
public class Region implements Feature {
  /** genome contig identifier */
  private final Contig contig;

  /** genome start position (inclusive, 1-based) */
  private final @Nullable Integer start;

  /** genome stop position (inclusive, 1-based) */
  private final @Nullable Integer stop;

  public Region(Contig contig, @Nullable Integer start, @Nullable Integer stop) {
    if ((start != null && (start < 0 || (contig.getLength() != null && start > contig.getLength())))
        || (stop != null && (stop < 0 || (contig.getLength() != null && stop > contig.getLength())))
        || ((start != null && stop != null) && stop < start)) {
      throw new IllegalArgumentException("invalid genomic interval");
    }
    this.contig = contig;
    this.start = start;
    this.stop = stop;
  }
}
