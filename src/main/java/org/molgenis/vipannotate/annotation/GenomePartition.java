package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class GenomePartition<T extends LocusAnnotation<U>, U> {
  public static final int NR_POS_BITS = 20;

  private GenomePartitionKey genomePartitionKey;
  private List<T> locusAnnotationList;

  public void clear() {
    genomePartitionKey = null;
    if (locusAnnotationList != null) {
      locusAnnotationList.clear();
    }
  }

  public void add(T nextLocusAnnotation) {
    if (locusAnnotationList == null) {
      locusAnnotationList = new ArrayList<>(1 << NR_POS_BITS);
    }
    locusAnnotationList.add(nextLocusAnnotation);
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
