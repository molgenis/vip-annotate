package org.molgenis.vipannotate.annotation.phylop;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;

@RequiredArgsConstructor
public class PhyloPBedFeatureToPhyloPAnnotatedPositionMapper {
  private final FastaIndex fastaIndex;

  public PhyloPAnnotatedPosition map(PhyloPBedFeature bedFeature) {
    FastaIndexRecord fastaIndexRecord = fastaIndex.get(bedFeature.chr());
    if (fastaIndexRecord == null) {
      throw new IllegalArgumentException("unknown contig '%s'".formatted(bedFeature.chr()));
    }
    Contig contig = new Contig(fastaIndexRecord.name(), fastaIndexRecord.length());

    int pos = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.score();
    return new PhyloPAnnotatedPosition(new Position(contig, pos), new DoubleValueAnnotation(score));
  }
}
