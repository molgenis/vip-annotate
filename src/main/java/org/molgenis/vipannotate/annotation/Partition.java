package org.molgenis.vipannotate.annotation;

import java.util.List;

/**
 * Partition of annotated genomic intervals
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
public record Partition<
    T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>(
    PartitionKey key, List<V> annotatedIntervals) {
  static final int NR_POS_BITS = 18;

  public int calcMaxPos() {
    Contig contig = key.contig();
    if (contig.getLength() == null) {
      throw new IllegalArgumentException("contig length is null");
    }

    int maxPosInContig = contig.getLength();
    boolean isLastBin = calcBin(maxPosInContig) == key.bin();

    int maxPos;
    if (isLastBin) {
      maxPos = Partition.calcPosInBin(maxPosInContig);
    } else {
      maxPos = 1 << Partition.NR_POS_BITS;
    }
    return maxPos;
  }

  private static int calcBin(int pos) {
    return pos >> NR_POS_BITS;
  }

  public static int calcPosInBin(int pos) {
    int bin = calcBin(pos);
    return pos - (bin << NR_POS_BITS);
  }

  public static int getPartitionStart(PartitionKey key, int pos) {
    return pos - (key.bin() << NR_POS_BITS);
  }
}
