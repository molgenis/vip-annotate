package org.molgenis.vcf.annotate.db.exact.formatv2;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationData;
import org.molgenis.vcf.annotate.db.exact.format.BigVariantIndexLookupTable;
import org.molgenis.vcf.annotate.db.exact.format.SmallVariantIndexLookupTable;

public record VariantAltAlleleAnnotationData(
    AnnotationData smallAnnotationData, AnnotationData bigAnnotationData) {
  public MemoryBuffer getVariant(VariantAltAllele variantAltAllele, int variantIndex) {
    MemoryBuffer annotationData;
    if (VariantAltAlleleEncoder.isSmallVariant(variantAltAllele)) {
      annotationData = smallAnnotationData.get(variantIndex);
    } else {
      annotationData = bigAnnotationData.get(variantIndex);
    }
    return annotationData;
  }
}
