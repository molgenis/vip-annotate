package org.molgenis.vipannotate.annotation.ncer;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;

public class NcERBedFeatureToContigPosAnnotationMapper {
  public List<NcERAnnotatedPosition> map(NcERBedFeature bedFeature) {
    Contig contig = new Contig(bedFeature.chr(), 1); // FIXME length
    int start = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    int end = bedFeature.end() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.perc();

    List<NcERAnnotatedPosition> contigPosAnnotations;
    if (end - start == 1) {
      contigPosAnnotations =
          List.of(
              new NcERAnnotatedPosition(new Position(contig, start), new DoubleValueAnnotation(score)));
    } else {
      contigPosAnnotations = new ArrayList<>(end - start);
      for (int pos = start; pos < end; pos++) {
        contigPosAnnotations.add(new NcERAnnotatedPosition(new Position(contig, pos), new DoubleValueAnnotation(score)));
      }
    }
    return contigPosAnnotations;
  }
}
