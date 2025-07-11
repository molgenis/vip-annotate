package org.molgenis.vipannotate.annotation;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndex implements AnnotationIndex<SequenceVariant> {
  @lombok.NonNull private SequenceVariantAnnotationIndexSmall sequenceVariantAnnotationIndexSmall;
  @lombok.NonNull private SequenceVariantAnnotationIndexBig sequenceVariantAnnotationIndexBig;

  public int findIndex(SequenceVariant feature) {
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
    if (SequenceVariantEncoder.isSmallVariant(feature)) {
      index = sequenceVariantAnnotationIndexSmall.findIndex(feature);
    } else {
      index = sequenceVariantAnnotationIndexBig.findIndex(feature);
    }
    return index;
  }
}
