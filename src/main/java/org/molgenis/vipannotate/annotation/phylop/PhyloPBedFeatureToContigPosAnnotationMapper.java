package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.ContigPosAnnotation;

public class PhyloPBedFeatureToContigPosAnnotationMapper {
  public ContigPosAnnotation map(PhyloPBedFeature bedFeature) {
    String contig = bedFeature.chr();
    int pos = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.score();
    return new ContigPosAnnotation(contig, pos, score);
  }
}
