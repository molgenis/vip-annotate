package org.molgenis.vipannotate.annotation;

import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class SequenceVariantAnnotationIndex implements AnnotationIndex<SequenceVariant> {
  private final SequenceVariantAnnotationIndexSmall sequenceVariantAnnotationIndexSmall;
  private final SequenceVariantAnnotationIndexBig sequenceVariantAnnotationIndexBig;

  public @Nullable IndexRange findIndexes(SequenceVariant feature) {
    // TODO refactor solve elsewhere
    boolean canDetermineIndex = true;
    if (feature.getType() == SequenceVariantType.STRUCTURAL
        || feature.getType() == SequenceVariantType.OTHER) {
      return null;
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
      return null;
    }

    IndexRange indexRange;
    if (SequenceVariantEncoder.isSmallVariant(feature)) {
      indexRange = sequenceVariantAnnotationIndexSmall.findIndex(feature);
    } else {
      indexRange = sequenceVariantAnnotationIndexBig.findIndex(feature);
    }
    return indexRange;
  }
}
