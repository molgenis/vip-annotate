package org.molgenis.vipannotate.db.exact.format;

import java.io.Serializable;
import java.util.Arrays;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.VariantEncoder;

public record SmallVariantIndexLookupTable(SortedIntArrayWrapper encodedVariants)
    implements Serializable {
  public int findIndex(Variant variant) {
    int encodedSmallVariant = VariantEncoder.encodeSmall(variant);
    int index = Arrays.binarySearch(encodedVariants.array(), encodedSmallVariant);
    return index >= 0 ? index : -1;
  }
}
