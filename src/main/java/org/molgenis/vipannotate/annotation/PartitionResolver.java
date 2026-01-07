package org.molgenis.vipannotate.annotation;

import org.jspecify.annotations.Nullable;

public class PartitionResolver {
  @Nullable private PartitionKey lastPartitionKey;

  public PartitionKey resolvePartitionKey(Contig contig, int pos) {
    return getOrCreatePartitionKey(contig, calcBin(pos));
  }

  public <T extends Interval> PartitionKey resolvePartitionKey(T interval) {
    return resolvePartitionKey(interval.getContig(), interval.getStart());
  }

  public <T extends Interval, U extends @Nullable Annotation, V extends AnnotatedInterval<T, U>>
      PartitionKey resolvePartitionKey(V annotatedInterval) {
    return resolvePartitionKey(annotatedInterval.getFeature());
  }

  /**
   * Returns the position relative to the partition
   *
   * @param pos genomic position within the contig of this key
   * @return the position relative to the partition
   */
  public int getPartitionPos(int pos) {
    return pos - (calcBin(pos) << Partition.NR_POS_BITS);
  }

  private int calcBin(int pos) {
    return pos >> Partition.NR_POS_BITS;
  }

  /**
   * Caches the last created {@link PartitionKey} to avoid redundant object creation which reduces
   * garbage collector pressure.
   */
  private PartitionKey getOrCreatePartitionKey(Contig contig, int bin) {
    if (lastPartitionKey == null
        || lastPartitionKey.bin() != bin
        || !lastPartitionKey.contig().equals(contig)) {
      lastPartitionKey = new PartitionKey(contig, bin);
    }
    return lastPartitionKey;
  }
}
