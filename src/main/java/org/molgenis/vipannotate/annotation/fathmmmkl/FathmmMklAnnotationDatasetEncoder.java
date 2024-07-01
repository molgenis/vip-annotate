package org.molgenis.vipannotate.annotation.fathmmmkl;

import org.molgenis.vipannotate.serialization.MemoryBuffer;
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
    MemoryBuffer memoryBuffer = MemoryBuffer.wrap(new byte[scoreIt.getSize()]);
    scoreIt.forEachRemaining(
        value -> {
          byte encodedValue = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(value);
          memoryBuffer.putByte(encodedValue);
        });
    return memoryBuffer;
  }
}
