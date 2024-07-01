package org.molgenis.vipannotate.db.exact.formatv2;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantEncoder;

public record VariantAltAlleleAnnotationIndexBig(BigInteger[] encodedVariants)
    implements Serializable {
  public int findIndex(Variant variant) {
    BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
