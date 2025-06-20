package org.molgenis.vipannotate.annotation.ncer;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.annotation.Position;
import org.molgenis.vipannotate.format.fasta.FastaIndex;
import org.molgenis.vipannotate.format.fasta.FastaIndexRecord;

@RequiredArgsConstructor
public class NcERBedFeatureToNcERAnnotatedPositionMapper {
  @NonNull private final FastaIndex fastaIndex;

  public List<NcERAnnotatedPosition> map(NcERBedFeature bedFeature) {
    FastaIndexRecord fastaIndexRecord = fastaIndex.get(bedFeature.chr());
    if (fastaIndexRecord == null) {
      throw new IllegalArgumentException("unknown contig '%s'".formatted(bedFeature.chr()));
    }
    Contig contig = new Contig(fastaIndexRecord.name(), fastaIndexRecord.length());

    int start = bedFeature.start() + 1; // 0-based .bed position to 1-based annotation position
    int end = bedFeature.end() + 1; // 0-based .bed position to 1-based annotation position
    double score = bedFeature.perc();

    List<NcERAnnotatedPosition> contigPosAnnotations;
    if (end - start == 1) {
      contigPosAnnotations =
          List.of(
              new NcERAnnotatedPosition(
                  new Position(contig, start), new DoubleValueAnnotation(score)));
    } else {
      contigPosAnnotations = new ArrayList<>(end - start);
      for (int pos = start; pos < end; pos++) {
        contigPosAnnotations.add(
            new NcERAnnotatedPosition(new Position(contig, pos), new DoubleValueAnnotation(score)));
      }
    }
    return contigPosAnnotations;
  }
}
