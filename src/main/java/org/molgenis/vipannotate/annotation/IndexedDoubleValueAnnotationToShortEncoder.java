package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

public class IndexedDoubleValueAnnotationToShortEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final double minValue;
  private final double maxValue;

  public IndexedDoubleValueAnnotationToShortEncoder(double minValue, double maxValue) {
    this(new DoubleCodec(), minValue, maxValue);
  }

  IndexedDoubleValueAnnotationToShortEncoder(
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
    return Short.BYTES;
  }

  @Override
  public void encode(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    short encodedScore = doubleCodec.encodeDoubleAsShort(score, minValue, maxValue);
    memoryBuffer.putInt16(indexedAnnotation.getIndex(), encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    short encodedNullScore = doubleCodec.encodeDoubleAsShort(null, minValue, maxValue);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
