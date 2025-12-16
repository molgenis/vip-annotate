package org.molgenis.vipannotate;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.Feature;

/**
 * Genomic interval [start, stop]
 *
 * @param contig genome contig identifier
 * @param start genome start position (inclusive, 1-based)
 * @param stop genome stop position (inclusive, 1-based)
 */
public record Region(Contig contig, @Nullable Integer start, @Nullable Integer stop)
    implements Feature {
  public Region {
    if ((start != null
            && (start <= 0 || (contig.getLength() != null && start > contig.getLength())))
        || (stop != null
            && (stop <= 0 || (contig.getLength() != null && stop > contig.getLength())))
        || ((start != null && stop != null) && stop < start)) {
      throw new IllegalArgumentException();
    }
  }

  public Region(Contig contig) {
    this(contig, null, null);
  }

  public Region(Contig contig, Integer start) {
    this(contig, start, null);
  }
}
