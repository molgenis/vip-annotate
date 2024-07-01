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

  public MemoryBuffer encodeGeneId(SizedIterator<@Nullable Integer> geneIdIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[geneIdIt.getSize()]);
    geneIdIt.forEachRemaining(
        geneId -> {
          byte encodedGeneId = geneId != null ? safeIntToByte(geneId) : (byte) -1;
          memoryBuffer.putByte(encodedGeneId);
        });
    return memoryBuffer;
  }

  public MemoryBuffer encodeScore(SizedIterator<Double> doubleIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[doubleIt.getSize()]);
    doubleIt.forEachRemaining(
        doubleValue -> {
          byte encodedScore = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(doubleValue);
          memoryBuffer.putByte(encodedScore);
        });
    return memoryBuffer;
  }

  /**
   * encode positions and write to a memory buffer
   *
   * @param posIt positions in the range [-50, 50]
   * @return memory buffer with encoded positions
   */
  public MemoryBuffer encodePos(SizedIterator<@Nullable Byte> posIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[posIt.getSize()]);
    posIt.forEachRemaining(
        byteValue -> {
          byte encodedPos = (byte) (byteValue != null ? byteValue + 51 : 0);
          memoryBuffer.putByte(encodedPos);
        });
    return memoryBuffer;
  }
}
