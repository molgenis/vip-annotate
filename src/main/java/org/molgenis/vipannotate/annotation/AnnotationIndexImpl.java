package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotationIndexImpl implements AnnotationIndex {
  @NonNull private VariantAnnotationIndexSmall variantAnnotationIndexSmall;
  @NonNull private VariantAnnotationIndexBig variantAnnotationIndexBig;

  public int findIndex(SequenceVariant variant) {
    // FIXME solve elsewhere
    boolean canDetermineIndex = true;
    for (byte altBase : variant.getAlt()) {

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
