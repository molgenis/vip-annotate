package org.molgenis.vipannotate.annotation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.spec.PositionBinSpec;

@RequiredArgsConstructor
public class PartitionResolver {
  private final Map<String, PositionBinSpec> partitioningSpec;

  @Nullable private PartitionKey lastPartitionKey;

  public PositionEncoding resolvePosition(PartitionKey key, int pos) {
    PositionBinSpec spec = getSpec(key.contig());
    long binSize = 1L << spec.bits();
    long partitionStart = spec.offset() + key.bin() * binSize;
    int position = Math.toIntExact((long) pos - partitionStart);
    return new PositionEncoding(position, spec.bits());
  }

  public PartitionKey resolvePartitionKey(Contig contig, int pos) {
    return getOrCreatePartitionKey(contig, calcBin(contig, pos));
  }

  public <T extends Interval> PartitionKey resolvePartitionKey(T interval) {
    return resolvePartitionKey(interval.getContig(), interval.getStart());
  }

  public <T extends Interval, U extends @Nullable Annotation, V extends AnnotatedInterval<T, U>>
      PartitionKey resolvePartitionKey(V annotatedInterval) {
    return resolvePartitionKey(annotatedInterval.getFeature());
  }

  /**
   * Returns the position relative to the partition.
   *
   * @param contig contig containing the position
   * @param pos genomic position within the contig
   * @return the position relative to the partition
   */
  public int getPartitionPos(Contig contig, int pos) {
    PositionBinSpec spec = getSpec(contig);
    long bin = calcBin(contig, pos);
    long binSize = 1L << spec.bits();

    return Math.toIntExact((long) pos - (spec.offset() + bin * binSize));
  }

  private int calcBin(Contig contig, int pos) {
    PositionBinSpec spec = getSpec(contig);
    long binSize = 1L << spec.bits();

    return Math.toIntExact(Math.floorDiv((long) pos - spec.offset(), binSize));
  }

  private PositionBinSpec getSpec(Contig contig) {
    PositionBinSpec spec = partitioningSpec.get(contig.getName());
    if (spec == null) {
      throw new IllegalArgumentException(
          "No partitioning specification for contig: " + contig.getName());
    }
    return spec;
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

  //  static final int NR_POS_BITS = 18;
  //
  //  public int calcMaxPos() {
  //    Contig contig = key.contig();
  //
  //    int maxPosInContig = contig.getLength();
  //    boolean isLastBin = calcBin(maxPosInContig) == key.bin();
  //
  //    int maxPos;
  //    if (isLastBin) {
  //      maxPos = Partition.calcPosInBin(maxPosInContig);
  //    } else {
  //      maxPos = 1 << Partition.NR_POS_BITS;
  //    }
  //    return maxPos;
  //  }
  //
  //  private static int calcBin(int pos) {
  //    return pos >> NR_POS_BITS;
  //  }
  //
  //  public static int calcPosInBin(int pos) {
  //    int bin = calcBin(pos);
  //    return pos - (bin << NR_POS_BITS);
  //  }
  //
  //  public static int getPartitionStart(PartitionKey key, int pos) {
  //    return pos - (key.bin() << NR_POS_BITS);
  //  }
}
