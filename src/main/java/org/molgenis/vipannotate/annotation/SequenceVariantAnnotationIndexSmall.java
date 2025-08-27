package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.SortedIntArrayWrapper;
import org.molgenis.vipannotate.util.IndexRange;
import org.molgenis.vipannotate.util.IndexRangeFinder;

public record SequenceVariantAnnotationIndexSmall(SortedIntArrayWrapper encodedVariants) {
  public @Nullable IndexRange findIndex(SequenceVariant variant) {
    int encodedSmallVariant = SequenceVariantEncoder.encodeSmall(variant);
    int[] encodedVariantsArray = encodedVariants.array();
    return IndexRangeFinder.findIndexes(encodedVariantsArray, encodedSmallVariant);
  }
}
