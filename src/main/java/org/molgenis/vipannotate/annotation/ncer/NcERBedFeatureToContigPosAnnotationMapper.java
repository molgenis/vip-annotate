package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.ContigPosAnnotation;

public class NcERBedFeatureToContigPosAnnotationMapper {
  public ContigPosAnnotation map(NcERBedFeature bedFeature) {
    String contig = bedFeature.chr();
    int pos = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.perc();
    return new ContigPosAnnotation(contig, pos, score);
  }
}
