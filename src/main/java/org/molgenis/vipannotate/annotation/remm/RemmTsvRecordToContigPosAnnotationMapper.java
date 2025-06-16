package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.annotation.ContigPosAnnotation;

public class RemmTsvRecordToContigPosAnnotationMapper {
  public ContigPosAnnotation map(RemmTsvRecord remmTsvRecord) {
    String contig = remmTsvRecord.chr();
    int pos = remmTsvRecord.start(); // 1-based
    double score = remmTsvRecord.score();
    return new ContigPosAnnotation(contig, pos, score);
  }
}
