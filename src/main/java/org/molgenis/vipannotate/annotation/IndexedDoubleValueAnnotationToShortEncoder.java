package org.molgenis.vipannotate.annotation;

import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.IndexRange;

public class IndexedDoubleValueAnnotationToShortEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final double minValue;
  private final double maxValue;

  public IndexedDoubleValueAnnotationToShortEncoder(
      DoubleCodec doubleCodec,
      double minValue,
      double maxValue) { // TODO replace with DoubleInterval
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
    int memoryBufferIndex = indexedAnnotation.getIndex() * getAnnotationSizeInBytes();
    short encodedScore = doubleCodec.encodeDoubleAsShort(score, minValue, maxValue);
    memoryBuffer.putInt16(memoryBufferIndex, encodedScore);
  }

  @Override
  public void clear(IndexRange indexRange, MemoryBuffer memoryBuffer) {
    short encodedNullScore = doubleCodec.encodeDoubleAsShort(null, minValue, maxValue);
    for (int i = indexRange.start(), indexEnd = indexRange.end(); i < indexEnd; i++) {
      int memoryBufferIndex = i * getAnnotationSizeInBytes();
      memoryBuffer.putInt16(memoryBufferIndex, encodedNullScore);
    }
  }
}
