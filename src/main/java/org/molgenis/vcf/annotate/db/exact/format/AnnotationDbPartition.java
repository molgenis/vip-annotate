package org.molgenis.vcf.annotate.db.exact.format;

import java.io.Serializable;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;

public record AnnotationDbPartition(
    SmallVariantIndexLookupTable smallVariantIndexLookupTable,
    AnnotationData smallAnnotationData,
    BigVariantIndexLookupTable bigVariantIndexLookupTable,
    AnnotationData bigAnnotationData)
    implements Serializable {
  public MemoryBuffer getVariant(VariantAltAllele variantAltAllele) {

    MemoryBuffer memoryBuffer;
    if (VariantAltAlleleEncoder.isSmallVariant(variantAltAllele)) {
      int index = smallVariantIndexLookupTable.findIndex(variantAltAllele);
      memoryBuffer = index != -1 ? smallAnnotationData.get(index) : null;
    } else {
      int index = bigVariantIndexLookupTable.findIndex(variantAltAllele);
      memoryBuffer = index != -1 ? bigAnnotationData.get(index) : null;
    }
    return memoryBuffer;
  }
}
