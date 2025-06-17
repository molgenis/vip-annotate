package org.molgenis.vipannotate.annotation.ncer;

import java.util.ArrayList;
import java.util.List;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;

public class NcERBedFeatureToContigPosAnnotationMapper {
  public List<ContigPosAnnotation> map(NcERBedFeature bedFeature) {
    String contig = bedFeature.chr();
    int start = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    int end = bedFeature.end() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.perc();

    List<ContigPosAnnotation> contigPosAnnotations;
    if (end - start == 1) {
      contigPosAnnotations = List.of(new ContigPosAnnotation(contig, start, score));
    } else {
      contigPosAnnotations = new ArrayList<>(end - start);
      for (int pos = start; pos < end; pos++) {
        contigPosAnnotations.add(new ContigPosAnnotation(contig, pos, score));
      }
    }
    return contigPosAnnotations;
  }
}
