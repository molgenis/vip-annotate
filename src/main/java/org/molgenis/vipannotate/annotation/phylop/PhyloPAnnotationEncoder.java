package org.molgenis.vipannotate.annotation.phylop;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.util.Encoder;

public class PhyloPAnnotationEncoder implements AnnotationEncoder<ContigPosAnnotation> {
  private static final double SCORE_MIN = -20.0d;
  private static final double SCORE_MAX = 10.003d;

  @Override
  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  @Override
  public void encode(
      int index, @NonNull ContigPosAnnotation annotation, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(index);

    Double score = annotation.score();
    short encodedScore = Encoder.encodeDoubleAsShort(score, SCORE_MIN, SCORE_MAX);
    memoryBuffer.putInt16(index, encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    short encodedNullScore = Encoder.encodeDoubleAsShort(null, SCORE_MIN, SCORE_MAX);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
