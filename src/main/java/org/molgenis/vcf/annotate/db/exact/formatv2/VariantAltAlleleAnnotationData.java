package org.molgenis.vcf.annotate.db.exact.formatv2;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.VariantEncoder;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationData;

public record VariantAltAlleleAnnotationData(
    AnnotationData smallAnnotationData, AnnotationData bigAnnotationData) {
  public MemoryBuffer getVariant(Variant variant, int variantIndex) {
    MemoryBuffer annotationData;
    if (VariantEncoder.isSmallVariant(variant)) {
      annotationData = smallAnnotationData.get(variantIndex);
    } else {
      annotationData = bigAnnotationData.get(variantIndex);
    }
    return annotationData;
  }
}
