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
  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    short encodedScore = memoryBuffer.getShortAtIndex(index);
    Double decodedScore = doubleCodec.decodeDoubleFromShort(encodedScore, valueInterval);
    return new DoubleValueAnnotation(decodedScore);
  }
}
