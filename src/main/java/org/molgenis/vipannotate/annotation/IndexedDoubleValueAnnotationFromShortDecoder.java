package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

@RequiredArgsConstructor
public class IndexedDoubleValueAnnotationFromShortDecoder
    implements AnnotationDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;
  private final DoubleInterval valueInterval;

  @Override
  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    short encodedScore = memoryBuffer.getInt16(index * Short.BYTES);
    Double decodedScore = doubleCodec.decodeDoubleFromShort(encodedScore, valueInterval);
    return new DoubleValueAnnotation(decodedScore);
  }
}
