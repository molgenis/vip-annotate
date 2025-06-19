package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

public class PhyloPBedFeatureToContigPosAnnotationMapper {
  public PhyloPAnnotatedPosition map(PhyloPBedFeature bedFeature) {
    Contig contig = new Contig(bedFeature.chr(), 1); // FIXME length
    int pos = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.score();
    return new PhyloPAnnotatedPosition(new Position(contig, pos), new DoubleValueAnnotation(score));
  }
}
