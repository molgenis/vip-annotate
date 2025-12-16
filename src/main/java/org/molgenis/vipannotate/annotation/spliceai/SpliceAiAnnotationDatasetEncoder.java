package org.molgenis.vipannotate.annotation.spliceai;

import static org.molgenis.vipannotate.util.Numbers.safeIntToByte;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.*;

public class SpliceAiAnnotationDatasetEncoder {

  private final DoubleCodec doubleCodec;

  public SpliceAiAnnotationDatasetEncoder() {
    this(new DoubleCodec());
  }

  SpliceAiAnnotationDatasetEncoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  /**
   * encode gene identifiers into the given memory buffer
   *
   * @param geneIdIt scores
   * @param memBuffer memory buffer with capacity >= {@link #calcEncodedScoreSize(SizedIterator)}
   */
  public void encodeGeneId(SizedIterator<@Nullable Integer> geneIdIt, MemoryBuffer memBuffer) {
    geneIdIt.forEachRemaining(
        geneId -> {
          byte encodedGeneId = geneId != null ? safeIntToByte(geneId) : (byte) -1;
          memBuffer.putByteUnchecked(encodedGeneId);
        });
  }

  public long calcEncodedGeneIdSize(SizedIterator<@Nullable Integer> geneIdIt) {
    return geneIdIt.getSize() * Byte.BYTES;
  }

  /**
   * encode scores into the given memory buffer
   *
   * @param scoreIt scores
   * @param memBuffer memory buffer with capacity >= {@link #calcEncodedScoreSize(SizedIterator)}
   */
  public void encodeScore(SizedIterator<Double> scoreIt, MemoryBuffer memBuffer) {
    scoreIt.forEachRemaining(
        doubleValue -> {
          byte encodedScore = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(doubleValue);
          memBuffer.putByteUnchecked(encodedScore);
        });
  }

  public long calcEncodedScoreSize(SizedIterator<Double> doubleIt) {
    return doubleIt.getSize() * Byte.BYTES;
  }

  /**
   * encode positions into the given memory buffer
   *
   * @param posIt positions in the range [-50, 50]
   * @param memBuffer memory buffer with capacity >= {@link #calcEncodedPosSize(SizedIterator)}
   */
  public void encodePos(SizedIterator<@Nullable Byte> posIt, MemoryBuffer memBuffer) {
    posIt.forEachRemaining(
        byteValue -> {
          byte encodedPos = (byte) (byteValue != null ? byteValue + 51 : 0);
          memBuffer.putByteUnchecked(encodedPos);
        });
  }

  public long calcEncodedPosSize(SizedIterator<@Nullable Byte> posIt) {
    return posIt.getSize() * Byte.BYTES;
  }
}
