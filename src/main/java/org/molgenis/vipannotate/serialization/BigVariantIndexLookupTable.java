package org.molgenis.vipannotate.serialization;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import org.molgenis.vipannotate.annotation.Variant;
import org.molgenis.vipannotate.annotation.VariantEncoder;

public record BigVariantIndexLookupTable(BigInteger[] encodedVariants) implements Serializable {
  public int findIndex(Variant variant) {
    BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
