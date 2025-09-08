package org.molgenis.vipannotate.annotation.fathmmmkl;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

public class FathmmMklAnnotationDatasetDecoder {
  private final DoubleCodec doubleCodec;

  public FathmmMklAnnotationDatasetDecoder() {
    this(new DoubleCodec());
  }

  FathmmMklAnnotationDatasetDecoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public double decodeScore(MemoryBuffer memoryBuffer, int index) {
      byte value = memoryBuffer.getByte(index * Byte.BYTES);
      return doubleCodec.decodeDoublePrimitiveUnitIntervalFromByte(value);
  }
}
