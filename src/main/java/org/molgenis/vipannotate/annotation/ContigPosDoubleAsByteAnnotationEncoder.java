package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.Encoder;

public class ContigPosDoubleAsByteAnnotationEncoder
    implements AnnotationEncoder<ContigPosAnnotation> {
  private final double minValue;
  private final double maxValue;

  public ContigPosDoubleAsByteAnnotationEncoder(double minValue, double maxValue) {
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
      int index, @NonNull ContigPosAnnotation annotation, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(index);

    Double score = annotation.score();
    byte encodedScore = Encoder.encodeDoubleAsByte(score, minValue, maxValue);
    memoryBuffer.putByte(index, encodedScore);
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
