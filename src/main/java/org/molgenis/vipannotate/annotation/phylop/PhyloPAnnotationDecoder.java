package org.molgenis.vipannotate.annotation.phylop;

import static org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationEncoder.SCORE_MAX;
import static org.molgenis.vipannotate.annotation.phylop.PhyloPAnnotationEncoder.SCORE_MIN;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.util.DoubleCodec;

@RequiredArgsConstructor
public class PhyloPAnnotationDecoder
    implements AnnotationDecoder<
        DoubleValueAnnotation> { // TODO extract generic decoder and use in ncer and remm
  private final DoubleCodec doubleCodec;

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    short encodedScore = memoryBuffer.getInt16(index * Short.BYTES);
    Double decodedScore = doubleCodec.decodeDoubleFromShort(encodedScore, SCORE_MIN, SCORE_MAX);
    return new DoubleValueAnnotation(decodedScore);
  }
}
