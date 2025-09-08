package org.molgenis.vipannotate.annotation.fathmmmkl;

import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.*;

public class FathmmMklAnnotationDatasetEncoder {
  private final DoubleCodec doubleCodec;

  public FathmmMklAnnotationDatasetEncoder() {
    this(new DoubleCodec());
  }

  FathmmMklAnnotationDatasetEncoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public MemoryBuffer encodeScores(SizedIterator<Double> scoreIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(scoreIt.getSize() * Byte.BYTES);
    scoreIt.forEachRemaining(
        value -> {
          byte encodedValue = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(value);
          memoryBuffer.writeByte(encodedValue);
        });
    return memoryBuffer;
  }
}
