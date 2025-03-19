package org.molgenis.vcf.annotate.db.exact.formatv2;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db.exact.VariantAltAllele;
import org.molgenis.vcf.annotate.db.exact.VariantAltAlleleEncoder;

public record VariantAltAlleleAnnotationIndexBig(BigInteger[] encodedVariants)
    implements Serializable {
  public int findIndex(VariantAltAllele variantAltAllele) {
    BigInteger encodedVariant = VariantAltAlleleEncoder.encodeBig(variantAltAllele);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
