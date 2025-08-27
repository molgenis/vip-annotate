package org.molgenis.vipannotate.annotation;

import java.io.Serializable;
import java.math.BigInteger;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;
import org.molgenis.vipannotate.util.IndexRangeFinder;

public record SequenceVariantAnnotationIndexBig(BigInteger[] encodedVariants)
    implements Serializable {
  public @Nullable IndexRange findIndex(SequenceVariant variant) {
    BigInteger encodedVariant = SequenceVariantEncoder.encodeBig(variant);
    return IndexRangeFinder.findIndexes(encodedVariants, encodedVariant);
  }
}
