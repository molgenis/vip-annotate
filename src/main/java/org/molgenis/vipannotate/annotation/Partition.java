package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

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
    Key key, List<V> annotatedIntervals) {
  private static final int NR_POS_BITS = 18;

  /** create a partition key from a contig and position */
  public static Key createKey(Contig contig, int pos) {
    int bin = calcBin(pos);
    return new Partition.Key(contig, bin);
  }

  /** create a partition key based on the start position from a genomic interval */
  public static <T extends Interval> Key createKey(T interval) {
    return createKey(interval.getContig(), interval.getStart());
  }

  /** create a partition key based on the start position from an annotated genomic interval */
  public static <T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
      Key createKey(V annotatedInterval) {
    return createKey(annotatedInterval.getFeature());
  }

  public int calcMaxPos() {
    int maxPosInContig = key.contig().getLength(); // FIXME
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

  public static int getPartitionStart(Key key, int pos) {
    return pos - (key.bin() << NR_POS_BITS);
  }

  /**
   * Annotated feature partition key
   *
   * @param contig contig
   * @param bin bin index
   */
  public record Key(Contig contig, int bin) {
    public Key {
      validateNonNegative(bin);
    }

    /**
     * @param pos genomic position within the contig of this key
     * @return the position relative to the partition
     */
    public int getPartitionPos(int pos) {
      return pos - (bin << NR_POS_BITS);
    }
  }
}
