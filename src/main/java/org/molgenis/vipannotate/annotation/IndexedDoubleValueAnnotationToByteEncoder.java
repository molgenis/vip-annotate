package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

public class IndexedDoubleValueAnnotationToByteEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final double minValue;
  private final double maxValue;

  public IndexedDoubleValueAnnotationToByteEncoder(double minValue, double maxValue) {
    this(new DoubleCodec(), minValue, maxValue);
  }

  IndexedDoubleValueAnnotationToByteEncoder(
      DoubleCodec doubleCodec, double minValue, double maxValue) {
    if (maxValue < minValue) {
      throw new IllegalArgumentException();
    }
    this.doubleCodec = doubleCodec;
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public int getAnnotationSizeInBytes() {
    return Byte.BYTES;
  }

  @Override
  public void encode(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    byte encodedScore = doubleCodec.encodeDoubleAsByte(score, minValue, maxValue);
    memoryBuffer.putByte(indexedAnnotation.getIndex(), encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    byte encodedNullScore = doubleCodec.encodeDoubleAsByte(null, minValue, maxValue);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
