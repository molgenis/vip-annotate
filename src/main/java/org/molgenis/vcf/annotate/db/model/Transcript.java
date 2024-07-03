package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;
import lombok.experimental.PackagePrivate;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Transcript extends ClosedInterval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull String id;
  @PackagePrivate int geneIndex;
  @NonNull Exon[] exons;
  Cds cds;

  /**
   * @return first exon overlapping with [start, stop] or <code>null</code>
   */
  public Exon findAnyExon(long start, long stop) {
    for (Exon exon : exons) {
      if (exon.isOverlapping(start, stop)) return exon;
    }
    return null;
  }
}
