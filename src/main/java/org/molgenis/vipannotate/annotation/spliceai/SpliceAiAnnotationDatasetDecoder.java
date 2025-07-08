package org.molgenis.vipannotate.annotation.spliceai;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

public class SpliceAiAnnotationDatasetDecoder {
  private final DoubleCodec doubleCodec;

  public SpliceAiAnnotationDatasetDecoder() {
    this(new DoubleCodec());
  }

  SpliceAiAnnotationDatasetDecoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public double decodeScore(MemoryBuffer memoryBuffer, int index) {
    byte value = memoryBuffer.getByte(index);
    return doubleCodec.decodeDoublePrimitiveUnitIntervalFromByte(value);
  }

  public byte decodePos(MemoryBuffer memoryBuffer, int index) {
    return (byte) (memoryBuffer.getByte(index) - 50);
  }
}
