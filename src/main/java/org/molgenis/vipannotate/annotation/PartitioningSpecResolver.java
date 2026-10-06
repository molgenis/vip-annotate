package org.molgenis.vipannotate.annotation;

import java.util.Map;
import org.molgenis.vipannotate.annotation.spec.PositionBinSpec;
import org.molgenis.vipannotate.util.Maps;

public class PartitioningSpecResolver {
  private static final byte MIN_POS_BITS = 1;
  private static final byte MAX_POS_BITS = 26;
  private static final long MAX_RECORDS_PER_BIN = 262144L;

  public Map<String, PositionBinSpec> resolve(Map<String, ContigAnalysis> contigAnalysisMap) {
    Map<String, PositionBinSpec> partitioningSpec =
        Maps.newLinkedHashMapWithExpectedSize(contigAnalysisMap.size());
    contigAnalysisMap.forEach(
        (key, contigAnalysis) -> partitioningSpec.put(key, createPositionBinSpec(contigAnalysis)));
    return partitioningSpec;
  }

  private static PositionBinSpec createPositionBinSpec(ContigAnalysis contigAnalysis) {
    long offset = contigAnalysis.minStartPos();
    byte binBits = chooseBinBits(contigAnalysis);
    return new PositionBinSpec(offset, binBits);
  }

  /** Returns number of bits to represent the span of [min, max] */
  private static byte bitsForRange(ContigAnalysis analysis) {
    long span = analysis.maxStartPos() - analysis.minStartPos() + 1;
    return (byte) Math.max(0, Long.SIZE - Long.numberOfLeadingZeros(span - 1));
  }

  private static byte chooseBinBits(ContigAnalysis analysis) {
    byte maxPosBitsForRange = bitsForRange(analysis);
    byte maxPosBits = (byte) Math.min(maxPosBitsForRange, MAX_POS_BITS);

    byte selected = maxPosBits;
    for (byte bits = MIN_POS_BITS; bits <= maxPosBits; bits++) {
      if (averageRecordsPerBin(analysis, bits) > MAX_RECORDS_PER_BIN) {
        break;
      }
      selected = bits;
    }
    return selected;
  }

  /**
   * Returns the average record per bin with bins normalized such that bin position 0 maps to
   * analysis.minStartPos().
   */
  private static long averageRecordsPerBin(ContigAnalysis analysis, byte binBits) {
    long binSize = 1L << binBits;
    long lastBin = Math.floorDiv(analysis.maxStartPos() - analysis.minStartPos(), binSize);
    long binCount = lastBin + 1;
    return Math.divideExact(analysis.recordCount(), binCount);
  }
}
