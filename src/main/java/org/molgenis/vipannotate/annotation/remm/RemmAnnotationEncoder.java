package org.molgenis.vipannotate.annotation.remm;

import static org.molgenis.vipannotate.util.Numbers.validateNonNegative;

import lombok.NonNull;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationEncoder;
import org.molgenis.vipannotate.annotation.ContigPosAnnotation;
import org.molgenis.vipannotate.util.Encoder;

public class RemmAnnotationEncoder implements AnnotationEncoder<ContigPosAnnotation> {
  @Override
  public int getAnnotationSizeInBytes() {
    return Byte.BYTES;
  }

  @Override
  public void encode(
      int index, @NonNull ContigPosAnnotation annotation, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(index);

    Double score = annotation.score();
    byte encodedScore = Encoder.encodeDoubleUnitIntervalAsByte(score);
    memoryBuffer.putByte(index, encodedScore);
  }

  @Override
  public void clear(int indexStart, int indexEnd, @NonNull MemoryBuffer memoryBuffer) {
    validateNonNegative(indexStart);
    validateNonNegative(indexEnd);
    if (indexEnd < indexStart) throw new IllegalArgumentException();

    byte encodedNullScore = Encoder.encodeDoubleUnitIntervalAsByte(null);
    for (int i = indexStart; i < indexEnd; i++) {
      memoryBuffer.putByte(i, encodedNullScore);
    }
  }
}
