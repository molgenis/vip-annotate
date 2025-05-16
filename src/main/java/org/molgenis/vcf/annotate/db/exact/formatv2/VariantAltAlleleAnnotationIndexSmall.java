package org.molgenis.vcf.annotate.db.exact.formatv2;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.VariantEncoder;
import org.molgenis.vcf.annotate.db.exact.format.SortedIntArrayWrapper;

public record VariantAltAlleleAnnotationIndexSmall(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(Variant variant) {
    int encodedSmallVariant = VariantEncoder.encodeSmall(variant);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
