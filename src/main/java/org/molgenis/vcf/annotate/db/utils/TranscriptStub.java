package org.molgenis.vcf.annotate.db.utils;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.molgenis.vcf.annotate.db.model.Cds;
import org.molgenis.vcf.annotate.db.model.ClosedInterval;
import org.molgenis.vcf.annotate.db.model.Exon;
import org.molgenis.vcf.annotate.db.model.Transcript;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TranscriptStub extends ClosedInterval {
  @NonNull String id;
  Integer geneIndex;
  @Singular @NonNull List<Exon> exons;
  @Singular @NonNull List<CdsStub> codingSequences;

  public Transcript createTranscript() {
    Transcript.TranscriptBuilder<?, ?> builder =
        Transcript.builder()
            .start(getStart())
            .length(getLength())
            .id(getId())
            .geneIndex(geneIndex != null ? geneIndex : -1)
            .exons(exons.toArray(Exon[]::new));
    if (!codingSequences.isEmpty()) {
      builder.cds(
          Cds.builder()
              .proteinId(codingSequences.getFirst().getProteinId())
              .parts(
                  codingSequences.stream()
                      .map(
                          cdsStub ->
                              Cds.Part.builder()
                                  .start(cdsStub.getStart())
                                  .length(cdsStub.getLength())
                                  .phase(cdsStub.getPhase())
                                  .build())
                      .toArray(Cds.Part[]::new))
              .build());
    }
    return builder.build();
  }
}
