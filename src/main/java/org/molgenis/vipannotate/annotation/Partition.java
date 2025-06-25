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
    org.molgenis.vipannotate.annotation.Partition.Key key, List<V> annotatedIntervals) {
  private static final int NR_POS_BITS = 20;

  public static <T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
      Key createKey(V annotatedInterval) {
    T interval = annotatedInterval.getFeature();
    return new Partition.Key(interval.getContig(), calcBin(interval.getStart()));
  }

  public int calcMaxPos() {
    int maxPosInContig = key.contig().getLength();
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

    public <T extends Interval> int getPartitionStart(T interval) {
      return Partition.getPartitionStart(this, interval.getStart());
    }

    public static <T extends Interval> Key create(T interval) {
      return Key.create(interval.getContig(), interval.getStart());
    }

    public static Key create(Contig contig, int pos) {
      return new Key(contig, calcBin(pos));
    }
  }
}
