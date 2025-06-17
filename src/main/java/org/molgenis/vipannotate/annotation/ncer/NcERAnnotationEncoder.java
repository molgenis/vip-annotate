package org.molgenis.vipannotate.annotation.ncer;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.util.Encoder;

public class NcERAnnotationEncoder implements AnnotationEncoder<ContigPosAnnotation> {
  private static final int PERC_MIN = 0;
  private static final int PERC_MAX = 100;

  @Override
  public int getAnnotationSizeInBytes() {
    return Short.BYTES;
  }

  @Override
  public void encode(
      int index, @NonNull ContigPosAnnotation annotation, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(index);

    Double score = annotation.score();
    short encodedScore = Encoder.encodeDoubleAsShort(score, PERC_MIN, PERC_MAX);
    memoryBuffer.putInt16(index, encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    short encodedNullScore = Encoder.encodeDoubleAsShort(null, PERC_MIN, PERC_MAX);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
