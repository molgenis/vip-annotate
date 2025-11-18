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

  public void encodeScores(SizedIterator<Double> scoreIt, MemoryBuffer memBuffer) {
    scoreIt.forEachRemaining(
        value -> {
          byte encodedValue = doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(value);
          memBuffer.putByteUnchecked(encodedValue);
        });
  }

  public long calcEncodedGeneIdSize(SizedIterator<Double> scoreIt) {
    return scoreIt.getSize() * Byte.BYTES;
  }
}
