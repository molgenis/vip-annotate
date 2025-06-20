package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.Encoder;

public class IndexedDoubleValueAnnotationToByteEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final double minValue;
  private final double maxValue;

  public IndexedDoubleValueAnnotationToByteEncoder(double minValue, double maxValue) {
    if (maxValue < minValue) {
      throw new IllegalArgumentException();
    }
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public int getAnnotationSizeInBytes() {
    return Byte.BYTES;
  }

  @Override
  public void encode(
      @NonNull IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation,
      @NonNull MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    byte encodedScore = Encoder.encodeDoubleAsByte(score, minValue, maxValue);
    memoryBuffer.putByte(indexedAnnotation.getIndex(), encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    byte encodedNullScore = Encoder.encodeDoubleAsByte(null, minValue, maxValue);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
