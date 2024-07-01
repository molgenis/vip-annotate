package org.molgenis.vcf.annotate.db.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Transcript extends Interval implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @NonNull Gene gene;
  @Singular @NonNull List<Exon> exons;
  @Singular @NonNull List<Cds> codingSequences;
  @Singular @NonNull List<TranscriptRef> transcriptRefs;

  public Strand getStrand() {
    return gene.getStrand();
  }

  public List<Exon> findExons(long start, long stop) {
    // TODO consider using interval tree
    return exons.stream()
        .filter(
            exon ->
                (start >= exon.getStart() && start <= exon.getStop())
                    || (stop >= exon.getStart() && stop <= exon.getStop())
                    || (start < exon.getStart() && stop > exon.getStop()))
        .toList();
  }

  public List<Cds> findCds(long start, long stop) {
    // TODO consider using interval tree
    return codingSequences.stream()
        .filter(
            codingSequence ->
                (start >= codingSequence.getStart() && start <= codingSequence.getStop())
                    || (stop >= codingSequence.getStart() && stop <= codingSequence.getStop())
                    || (start < codingSequence.getStart() && stop > codingSequence.getStop()))
        .toList();
  }
}
