package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.Encoder;

@RequiredArgsConstructor
public class IndexedDoubleValueAnnotationToShortEncoder
    implements IndexedAnnotationEncoder<DoubleValueAnnotation> {
  private final double minValue;
  private final double maxValue;

  @Override
  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  @Override
  public void encode(
      IndexedAnnotation<DoubleValueAnnotation> indexedAnnotation, MemoryBuffer memoryBuffer) {
    Double score = indexedAnnotation.getFeatureAnnotation().score();
    short encodedScore = Encoder.encodeDoubleAsShort(score, minValue, maxValue);
    memoryBuffer.putInt16(indexedAnnotation.getIndex(), encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    short encodedNullScore = Encoder.encodeDoubleAsShort(null, minValue, maxValue);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
