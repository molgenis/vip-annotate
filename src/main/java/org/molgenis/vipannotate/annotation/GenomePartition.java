package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class GenomePartition<T extends IntervalAnnotation<U>, U> {
  public static final int NR_POS_BITS = 20;

  private GenomePartitionKey genomePartitionKey;
  private List<T> intervalAnnotationList;

  public void clear() {
    genomePartitionKey = null;
    if (intervalAnnotationList != null) {
      intervalAnnotationList.clear();
    }
  }

  public void add(T intervalAnnotation) {
    if (intervalAnnotationList == null) {
      intervalAnnotationList = new ArrayList<>(1 << NR_POS_BITS);
    }
    intervalAnnotationList.add(intervalAnnotation);
  }

  public static int calcBin(int pos) {
    return pos >> NR_POS_BITS;
  }

  public static int calcPosInBin(int pos) {
    int bin = calcBin(pos);
    return pos - (bin << NR_POS_BITS);
  }

  public static int getPartitionStart(GenomePartitionKey genomePartitionKey, int pos) {
    return pos - (genomePartitionKey.bin() << NR_POS_BITS);
  }
}
