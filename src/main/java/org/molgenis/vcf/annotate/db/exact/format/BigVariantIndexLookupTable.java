package org.molgenis.vcf.annotate.db.exact.format;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;

public record BigVariantIndexLookupTable(BigInteger[] encodedVariants) implements Serializable {
  public int findIndex(VariantAltAllele variantAltAllele) {
    BigInteger encodedVariant = VariantAltAlleleEncoder.encodeBig(variantAltAllele);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
