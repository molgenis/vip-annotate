package org.molgenis.vipannotate.annotation;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.Encoder;

@RequiredArgsConstructor
public class ContigPosDoubleAsShortAnnotationEncoder
    implements AnnotationEncoder<ContigPosAnnotation> {
  private final double minValue;
  private final double maxValue;

  @Override
  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  @Override
  public void encode(
      int index, @NonNull ContigPosAnnotation annotation, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(index);

    Double score = annotation.score();
    short encodedScore = Encoder.encodeDoubleAsShort(score, minValue, maxValue);
    memoryBuffer.putInt16(index, encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    short encodedNullScore = Encoder.encodeDoubleAsShort(null, minValue, maxValue);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
