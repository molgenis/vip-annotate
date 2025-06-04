package org.molgenis.vipannotate.db.exact.formatv2;

import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantEncoder;
import org.molgenis.vipannotate.db.exact.format.SortedIntArrayWrapper;

public record VariantAltAlleleAnnotationIndex(
    VariantAltAlleleAnnotationIndexSmall variantAltAlleleAnnotationIndexSmall,
    VariantAltAlleleAnnotationIndexBig variantAltAlleleAnnotationIndexBig,
    SortedIntArrayWrapper variantOffsets) {

  public int findDataOffset(Variant variant) {
    int variantAnnotationDataOffset;
    if (VariantEncoder.isSmallVariant(variant)) {
      int index = variantAltAlleleAnnotationIndexSmall.findIndex(variant);
      variantAnnotationDataOffset = index != -1 ? variantOffsets.array()[index] : -1;
    } else {
      int index = variantAltAlleleAnnotationIndexBig.findIndex(variant);
      variantAnnotationDataOffset =
          index != -1
              ? variantOffsets
                  .array()[
                  variantAltAlleleAnnotationIndexSmall.encodedVariants().array().length + index]
              : -1;
    }
    return variantAnnotationDataOffset;
  }
}
