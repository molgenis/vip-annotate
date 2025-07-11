package org.molgenis.vipannotate.annotation;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequenceVariantGeneContextAnnotationIndex
    implements AnnotationIndex<SequenceVariantGeneContext> {
  @lombok.NonNull private SequenceVariantGeneContextAnnotationIndexSmall annotationIndexSmall;
  @lombok.NonNull private SequenceVariantGeneContextAnnotationIndexBig annotationIndexBig;

  public int findIndex(SequenceVariantGeneContext feature) {
    // TODO refactor solve elsewhere
    boolean canDetermineIndex = true;
    if (feature.getType() == SequenceVariantType.STRUCTURAL
        || feature.getType() == SequenceVariantType.OTHER) {
      return -1;
    } else {

      for (byte altBase : feature.getAlt().alt().getBytes(StandardCharsets.UTF_8)) {

        boolean isActg =
            switch (altBase) {
              case 'A', 'C', 'T', 'G' -> true;
              default -> false;
            };
        if (!isActg) {
          canDetermineIndex = false;
          break;
        }
      }
    }
    if (!canDetermineIndex) {
      return -1;
    }

    int index;
    if (SequenceVariantGeneContextEncoder.isSmallVariant(feature)) {
      index = annotationIndexSmall.findIndex(feature);
    } else {
      index = annotationIndexBig.findIndex(feature);
    }
    return index;
  }
}
