package org.molgenis.vcf.annotate.db2.exact.format;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db2.exact.VariantAltAlleleEncoder;

public record BigVariantIndexLookupTable(BigInteger[] encodedVariants) implements Serializable {
  public int findIndex(VariantAltAllele variantAltAllele) {
    BigInteger encodedVariant = VariantAltAlleleEncoder.encodeBig(variantAltAllele);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
