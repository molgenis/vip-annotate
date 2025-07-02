package org.molgenis.vipannotate.annotation;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotationIndexImpl implements AnnotationIndex {
  @lombok.NonNull private VariantAnnotationIndexSmall variantAnnotationIndexSmall;
  @lombok.NonNull private VariantAnnotationIndexBig variantAnnotationIndexBig;

  public int findIndex(SequenceVariant variant) {
    // TODO refactor solve elsewhere
    boolean canDetermineIndex = true;
    if (variant.getType() == SequenceVariantType.STRUCTURAL
        || variant.getType() == SequenceVariantType.OTHER) {
      return -1;
    } else {

      for (byte altBase : variant.getAlt().alt().getBytes(StandardCharsets.UTF_8)) {

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
    if (VariantEncoder.isSmallVariant(variant)) {
      index = variantAnnotationIndexSmall.findIndex(variant);
    } else {
      index = variantAnnotationIndexBig.findIndex(variant);
    }
    return index;
  }
}
