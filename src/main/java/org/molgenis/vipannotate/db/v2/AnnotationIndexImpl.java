package org.molgenis.vipannotate.db.v2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantEncoder;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexBig;
import org.molgenis.vipannotate.db.exact.formatv2.VariantAltAlleleAnnotationIndexSmall;

@RequiredArgsConstructor
public class AnnotationIndexImpl implements AnnotationIndex {
  @NonNull private VariantAltAlleleAnnotationIndexSmall variantAnnotationIndexSmall;
  @NonNull private VariantAltAlleleAnnotationIndexBig variantAnnotationIndexBig;

  public int findIndex(Variant variant) {
    // FIXME solve elsewhere
    boolean canDetermineIndex = true;
    for (byte altBase : variant.alt()) {

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
