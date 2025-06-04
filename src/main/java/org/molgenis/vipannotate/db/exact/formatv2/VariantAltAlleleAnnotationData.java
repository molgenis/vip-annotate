package org.molgenis.vipannotate.db.exact.formatv2;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantEncoder;
import org.molgenis.vipannotate.db.exact.format.AnnotationData;

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
