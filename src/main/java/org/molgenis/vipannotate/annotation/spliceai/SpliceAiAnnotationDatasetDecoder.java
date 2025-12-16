package org.molgenis.vipannotate.annotation.spliceai;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

public class SpliceAiAnnotationDatasetDecoder {
  private final DoubleCodec doubleCodec;

  public SpliceAiAnnotationDatasetDecoder() {
    this(new DoubleCodec());
  }

  SpliceAiAnnotationDatasetDecoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public int decodeGeneIndex(MemoryBuffer memoryBuffer, int index) {
    return memoryBuffer.getIntAtIndex(index);
  }

  public int decodeGeneRef(MemoryBuffer memoryBuffer, int index) {
    return memoryBuffer.getByteAtIndex(index);
  }

  public double decodeScore(MemoryBuffer memoryBuffer, int index) {
    byte value = memoryBuffer.getByteAtIndex(index);
    return doubleCodec.decodeDoublePrimitiveUnitIntervalFromByte(value);
  }

  public @Nullable Byte decodePos(MemoryBuffer memoryBuffer, int index) {
    byte b = memoryBuffer.getByteAtIndex(index);
    return b != 0 ? (byte) (b - 51) : null;
  }
}
