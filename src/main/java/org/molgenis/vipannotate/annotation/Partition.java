package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Partition of annotated genomic intervals
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
@Getter
@Setter
public class Partition<
    T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>> {
  public static final int NR_POS_BITS = 20;

  private Key key;
  private List<V> annotatedIntervals;

  public void clear() {
    key = null;
    if (annotatedIntervals != null) {
      annotatedIntervals.clear();
    }
  }

  public void add(V annotatedInterval) {
    if (annotatedIntervals == null) {
      annotatedIntervals = new ArrayList<>(1 << NR_POS_BITS);
    }
    annotatedIntervals.add(annotatedInterval);
  }

  public int calcMaxAnnotations() {
    int maxPosInContig = key.contig().getLength();
    boolean isLastBin = Partition.calcBin(maxPosInContig) == key.bin();

    int maxAnnotations;
    if (isLastBin) {
      maxAnnotations = Partition.calcPosInBin(maxPosInContig);
    } else {
      maxAnnotations = 1 << Partition.NR_POS_BITS;
    }
    return maxAnnotations;
  }

  public static int calcBin(int pos) {
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
  public record Key(@NonNull Contig contig, int bin) {
    public Key {
      validateNonNegative(bin);
    }

    public <T extends Interval> int getPartitionStart(@NonNull T interval) {
      return Partition.getPartitionStart(this, interval.getStart());
    }

    public static <T extends Interval> Key create(@NonNull T interval) {
      return Key.create(interval.getContig(), interval.getStart());
    }

    public static Key create(@NonNull Contig contig, int pos) {
      return new Key(contig, calcBin(pos));
    }
  }
}
