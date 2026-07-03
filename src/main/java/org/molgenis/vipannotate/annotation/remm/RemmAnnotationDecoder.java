package org.molgenis.vipannotate.annotation.remm;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.AnnotationDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;

@RequiredArgsConstructor
public class RemmAnnotationDecoder implements AnnotationDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;

  @Override
  public DoubleValueAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    byte encodedScore = memBuffer.getByteAtIndex(annotationIndex);
    Double decodedScore = doubleCodec.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, DoubleValueAnnotation annotation) {
    throw new RuntimeException("not implemented"); // FIXME
  }
}
