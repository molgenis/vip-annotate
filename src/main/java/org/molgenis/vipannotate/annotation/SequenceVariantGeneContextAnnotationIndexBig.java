package org.molgenis.vipannotate.annotation;

import java.math.BigInteger;
import java.util.Arrays;

@Deprecated
public record SequenceVariantGeneContextAnnotationIndexBig(BigInteger[] encodedVariants) {
  public int findIndex(SequenceVariant variant) {
    BigInteger encodedVariant = SequenceVariantEncoder.encodeBig(variant);
    int index = Arrays.binarySearch(encodedVariants, encodedVariant);
    return index >= 0 ? index : -1;
  }
}
