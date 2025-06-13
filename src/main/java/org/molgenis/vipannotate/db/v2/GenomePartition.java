package org.molgenis.vipannotate.db.v2;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class GenomePartition<T> {
  private static final int NR_POS_BITS = 20;

  private GenomePartitionKey genomePartitionKey;
  private List<VariantAnnotation<T>> variantAnnotationList;

  public void clear() {
    genomePartitionKey = null;
    if (variantAnnotationList != null) {
      variantAnnotationList.clear();
    }
  }

  public void add(VariantAnnotation<T> nextVariantAnnotation) {
    if (variantAnnotationList == null) {
      variantAnnotationList = new ArrayList<>(1 << NR_POS_BITS);
    }
    variantAnnotationList.add(nextVariantAnnotation);
  }

  public static int calcBin(int pos) {
    return pos >> NR_POS_BITS;
  }
}
