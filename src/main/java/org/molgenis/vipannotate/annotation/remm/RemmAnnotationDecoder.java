package org.molgenis.vipannotate.annotation.remm;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.util.DoubleCodec;

@RequiredArgsConstructor
public class RemmAnnotationDecoder implements AnnotationDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = doubleCodec.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }
}
