package org.molgenis.vcf.annotate.db.exact.format;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;

public record SmallVariantIndexLookupTable(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(VariantAltAllele variantAltAllele) {
    int encodedSmallVariant = VariantAltAlleleEncoder.encodeSmall(variantAltAllele);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
