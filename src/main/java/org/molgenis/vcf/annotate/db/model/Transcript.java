package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;
import lombok.experimental.PackagePrivate;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Transcript extends Interval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull String id;

  @PackagePrivate int geneIndex;

  // TODO creates ImmutableList of ArrayList --> replace with Exon[]
  @Singular @NonNull List<Exon> exons;
  @Singular @NonNull List<Cds> codingSequences;

  public Exon findAnyExon(long start, long stop) {
    for (Exon exon : exons) {
      if (isOverlapping(start, stop, exon)) return exon;
    }
    return null;
  }

  public List<Exon> findExons(long start, long stop) {
    // TODO 20% time is spend in this function
    // TODO consider using interval tree
    return exons.stream()
        .filter(
            exon ->
                (start >= exon.getStart() && start <= exon.getStop())
                    || (stop >= exon.getStart() && stop <= exon.getStop())
                    || (start < exon.getStart() && stop > exon.getStop()))
        .toList();
  }

  public Cds findAnyCds(long start, long stop) {
    for (Cds cds : codingSequences) {
      if (isOverlapping(start, stop, cds)) return cds;
    }
    return null;
  }

  public List<Cds> findCds(long start, long stop) {
    // TODO 20% time is spend in this function
    // TODO consider using interval tree
    return codingSequences.stream()
        .filter(
            codingSequence ->
                (start >= codingSequence.getStart() && start <= codingSequence.getStop())
                    || (stop >= codingSequence.getStart() && stop <= codingSequence.getStop())
                    || (start < codingSequence.getStart() && stop > codingSequence.getStop()))
        .toList();
  }

  private static boolean isOverlapping(long start, long stop, Interval interval) {
    return ((start >= interval.getStart() && start <= interval.getStop())
        || (stop >= interval.getStart() && stop <= interval.getStop())
        || (start < interval.getStart() && stop > interval.getStop()));
  }
}
