package org.molgenis.vcf.annotate.db.exact.formatv2;

import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;
import org.molgenis.vcf.annotate.db.exact.format.SortedIntArrayWrapper;

public record VariantAltAlleleAnnotationIndex(
    VariantAltAlleleAnnotationIndexSmall variantAltAlleleAnnotationIndexSmall,
    VariantAltAlleleAnnotationIndexBig variantAltAlleleAnnotationIndexBig,
    SortedIntArrayWrapper variantOffsets) {

  public int findDataOffset(VariantAltAllele variantAltAllele) {
    int variantAnnotationDataOffset;
    if (VariantAltAlleleEncoder.isSmallVariant(variantAltAllele)) {
      int index = variantAltAlleleAnnotationIndexSmall.findIndex(variantAltAllele);
      variantAnnotationDataOffset = index != -1 ? variantOffsets.array()[index] : -1;
    } else {
      int index = variantAltAlleleAnnotationIndexBig.findIndex(variantAltAllele);
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
