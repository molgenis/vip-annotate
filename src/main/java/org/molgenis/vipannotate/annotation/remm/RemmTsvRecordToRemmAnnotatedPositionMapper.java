package org.molgenis.vipannotate.annotation.remm;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;

@RequiredArgsConstructor
public class RemmTsvRecordToRemmAnnotatedPositionMapper {
  private final FastaIndex fastaIndex;

  public RemmAnnotatedPosition map(RemmTsvRecord remmTsvRecord) {
    FastaIndexRecord fastaIndexRecord = fastaIndex.get(remmTsvRecord.chr());
    if (fastaIndexRecord == null) {
      throw new IllegalArgumentException("unknown contig '%s'".formatted(remmTsvRecord.chr()));
    }
    Contig contig = new Contig(fastaIndexRecord.name(), fastaIndexRecord.length());

    int pos = remmTsvRecord.start(); // 1-based
    double score = remmTsvRecord.score();

    Position genomicFeature = new Position(contig, pos);
    DoubleValueAnnotation featureAnnotation = new DoubleValueAnnotation(score);
    return new RemmAnnotatedPosition(genomicFeature, featureAnnotation);
  }
}
