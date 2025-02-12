package org.molgenis.vcf.annotate.db2.exact.format;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAlleleEncoder;

public record SmallVariantIndexLookupTable(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(VariantAltAllele variantAltAllele) {
    int encodedSmallVariant = VariantAltAlleleEncoder.encodeSmall(variantAltAllele);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
