package org.molgenis.vipannotate.annotation;

import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.IndexRange;

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
    memoryBuffer.putByte(indexedAnnotation.getIndex() * getAnnotationSizeInBytes(), encodedScore);
  }

  @Override
  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    byte encodedNullScore = doubleCodec.encodeDoubleAsByte(null, minValue, maxValue);
    for (int i = indexRange.start(), indexEnd = indexRange.end(); i < indexEnd; i++) {
      memoryBuffer.putByte(i * getAnnotationSizeInBytes(), encodedNullScore);
    }
  }
}
