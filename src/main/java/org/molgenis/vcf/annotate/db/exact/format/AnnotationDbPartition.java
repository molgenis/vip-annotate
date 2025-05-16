package org.molgenis.vcf.annotate.db.exact.format;

import java.io.Serializable;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.VariantEncoder;

public record AnnotationDbPartition(
    SmallVariantIndexLookupTable smallVariantIndexLookupTable,
    AnnotationData smallAnnotationData,
    BigVariantIndexLookupTable bigVariantIndexLookupTable,
    AnnotationData bigAnnotationData)
    implements Serializable {
  public MemoryBuffer getVariant(Variant variant) {

    MemoryBuffer memoryBuffer;
    if (VariantEncoder.isSmallVariant(variant)) {
      int index = smallVariantIndexLookupTable.findIndex(variant);
      memoryBuffer = index != -1 ? smallAnnotationData.get(index) : null;
    } else {
      int index = bigVariantIndexLookupTable.findIndex(variant);
      memoryBuffer = index != -1 ? bigAnnotationData.get(index) : null;
    }
    return memoryBuffer;
  }
}
