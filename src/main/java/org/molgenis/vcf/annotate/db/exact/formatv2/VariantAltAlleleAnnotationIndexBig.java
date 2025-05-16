package org.molgenis.vcf.annotate.db.exact.formatv2;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.VariantEncoder;

public record VariantAltAlleleAnnotationIndexBig(BigInteger[] encodedVariants)
    implements Serializable {
  public int findIndex(Variant variant) {
    BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
