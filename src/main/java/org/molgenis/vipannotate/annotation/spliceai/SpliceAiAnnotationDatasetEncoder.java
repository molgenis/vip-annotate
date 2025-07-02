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
          byte encodedValue = doubleCodec.encodeDoubleUnitIntervalAsByte(value);
          memoryBuffer.writeByte(encodedValue);
        });
    return memoryBuffer;
  }

  public MemoryBuffer encodePos(SizedIterator<Byte> posIt) {
    MemoryBuffer memoryBuffer = MemoryBuffer.newHeapBuffer(posIt.getSize() * Byte.BYTES);
    posIt.forEachRemaining(memoryBuffer::writeByte);
    return memoryBuffer;
  }
}
