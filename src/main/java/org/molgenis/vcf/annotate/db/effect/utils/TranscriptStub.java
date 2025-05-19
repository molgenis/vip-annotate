package org.molgenis.vcf.annotate.db.effect.utils;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.molgenis.vcf.annotate.db.effect.model.*;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TranscriptStub extends FuryFactory.ClosedInterval {
  @NonNull String id;
  @NonNull FuryFactory.Transcript.Type type;
  Integer geneIndex;
  @Singular @NonNull List<FuryFactory.Exon> exons;
  @Singular @NonNull List<CdsStub> codingSequences;

  public FuryFactory.Transcript createTranscript() {
    FuryFactory.Transcript.TranscriptBuilder<?, ?> builder =
        FuryFactory.Transcript.builder()
            .start(getStart())
            .length(getLength())
            .id(getId())
            .type(getType())
            .geneIndex(geneIndex != null ? geneIndex : -1)
            .exons(exons.toArray(FuryFactory.Exon[]::new));
    if (!codingSequences.isEmpty()) {
      builder.cds(
          FuryFactory.Cds.builder()
              .proteinId(codingSequences.getFirst().getProteinId())
              .fragments(
                  codingSequences.stream()
                      .map(
                          cdsStub ->
                              FuryFactory.Cds.Fragment.builder()
                                  .start(cdsStub.getStart())
                                  .length(cdsStub.getLength())
                                  .phase(cdsStub.getPhase())
                                  .build())
                      .toArray(FuryFactory.Cds.Fragment[]::new))
              .build());
    }
    return builder.build();
  }
}
