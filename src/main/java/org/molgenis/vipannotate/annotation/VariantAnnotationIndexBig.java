package org.molgenis.vipannotate.annotation;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public record VariantAnnotationIndexBig(BigInteger[] encodedVariants) implements Serializable {
  public int findIndex(SequenceVariant variant) {
    BigInteger encodedVariant = VariantEncoder.encodeBig(variant);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
