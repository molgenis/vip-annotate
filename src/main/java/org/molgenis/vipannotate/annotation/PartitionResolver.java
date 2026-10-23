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
    int position = Math.toIntExact(toZeroBased(pos) - partitionStart);
    return new PositionEncoding(position, spec.bits());
  }

  public PartitionKey resolvePartitionKey(Contig contig, int pos) {
    int bin = calcBin(contig, pos);
    return getOrCreatePartitionKey(contig, bin);
  }

  public <T extends Interval> PartitionKey resolvePartitionKey(T interval) {
    return resolvePartitionKey(interval.getContig(), interval.getStart());
  }

  public <T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
      PartitionKey resolvePartitionKey(V annotatedInterval) {
    return resolvePartitionKey(annotatedInterval.getFeature());
  }

  /**
   * Returns the position relative to the partition.
   *
   * @param contig contig containing the position
   * @param pos genomic position within the contig (1-based)
   * @return the position relative to the partition
   */
  public int getPartitionPos(Contig contig, int pos) {
    PositionBinSpec spec = getSpec(contig);
    long bin = calcBin(contig, pos);
    if (bin == -1) {
      throw new IllegalArgumentException();
    }
    long binSize = 1L << spec.bits();

    return Math.toIntExact(toZeroBased(pos) - (spec.offset() + bin * binSize));
  }

  private long toZeroBased(int pos) {
    return (long) pos - 1;
  }

  /**
   * @return bin or {@code -1} if no bin exists for given position.
   */
  private int calcBin(Contig contig, int pos) {
    PositionBinSpec spec = getSpec(contig);

    long zeroBasedPos = pos - 1L;
    if (zeroBasedPos < spec.offset() || zeroBasedPos >= spec.endExclusive()) {
      return -1;
    }

    long binSize = 1L << spec.bits();
    return Math.toIntExact(Math.floorDiv(zeroBasedPos - spec.offset(), binSize));
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
}
