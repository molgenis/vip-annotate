package org.molgenis.vipannotate.annotation.spliceai;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.*;

public class SpliceAiAnnotationDatasetEncoder {

  private final DoubleCodec doubleCodec;

  public SpliceAiAnnotationDatasetEncoder() {
    this(new DoubleCodec());
  }

  SpliceAiAnnotationDatasetEncoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public MemoryBuffer encodeScore(SizedIterator<Double> doubleIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(doubleIt.getSize() * Byte.BYTES);
    doubleIt.forEachRemaining(
        value -> {
          byte encodedValue = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(value);
          memoryBuffer.writeByte(encodedValue);
        });
    return memoryBuffer;
  }

  /**
   * encode positions and write to a memory buffer
   *
   * @param posIt positions in the range [-50, 50]
   * @return memory buffer with encoded positions
   */
  public MemoryBuffer encodePos(SizedIterator<Byte> posIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(posIt.getSize() * Byte.BYTES);
    posIt.forEachRemaining(value -> memoryBuffer.writeByte(value + 50));
    return memoryBuffer;
  }
}
