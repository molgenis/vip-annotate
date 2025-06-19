package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

public class RemmTsvRecordToRemmAnnotationMapper {
  public RemmAnnotatedPosition map(RemmTsvRecord remmTsvRecord) {
    String tsvContig = remmTsvRecord.chr();
    int pos = remmTsvRecord.start(); // 1-based
    double score = remmTsvRecord.score();

    Contig contig = new Contig(tsvContig, 1); // FIXME get contig from fasta
    Position genomicFeature = new Position(contig, pos);
    DoubleValueAnnotation featureAnnotation = new DoubleValueAnnotation(score);
    return new RemmAnnotatedPosition(genomicFeature, featureAnnotation);
  }
}
