package org.molgenis.vipannotate.annotation;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;

public record SequenceVariantGeneContextAnnotationIndexSmall(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(SequenceVariantGeneContext variant) {
    long encodedSmallVariant = SequenceVariantGeneContextEncoder.encodeSmall(variant);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
