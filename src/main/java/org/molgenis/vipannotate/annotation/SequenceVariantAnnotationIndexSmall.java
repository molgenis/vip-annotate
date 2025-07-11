package org.molgenis.vipannotate.annotation;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;

public record SequenceVariantAnnotationIndexSmall(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(SequenceVariant variant) {
    int encodedSmallVariant = SequenceVariantEncoder.encodeSmall(variant);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
