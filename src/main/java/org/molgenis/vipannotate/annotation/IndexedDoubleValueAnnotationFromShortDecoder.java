package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

@RequiredArgsConstructor
public class IndexedDoubleValueAnnotationFromShortDecoder
    implements AnnotationDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final DoubleInterval valueInterval;

  @Override
  public DoubleValueAnnotation decode(MemoryBuffer memBuffer, int annotationIndex) {
    short encodedScore = memBuffer.getShortAtIndex(annotationIndex);
    Double decodedScore = doubleCodec.decodeDoubleFromShort(encodedScore, valueInterval);
    return new DoubleValueAnnotation(decodedScore);
  }

  @Override
  public void decodeInto(
      MemoryBuffer memBuffer, int annotationIndex, DoubleValueAnnotation annotation) {
    throw new RuntimeException("not implemented"); // FIXME
  }
}
