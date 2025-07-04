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
    Double doubleValue =
        doubleCodec.decodeDoubleUnitIntervalFromByte(value); // FIXME decode primitive
    if (doubleValue == null) {
      throw new NullPointerException();
    }
    return doubleValue;
  }

  public byte decodePos(MemoryBuffer memoryBuffer, int index) {
    return memoryBuffer.getByte(index);
  }
}
