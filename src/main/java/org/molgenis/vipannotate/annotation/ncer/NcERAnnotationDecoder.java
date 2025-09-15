package org.molgenis.vipannotate.annotation.ncer;

import static org.molgenis.vipannotate.annotation.ncer.NcERAnnotationEncoder.PERC_MAX;
import static org.molgenis.vipannotate.annotation.ncer.NcERAnnotationEncoder.PERC_MIN;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.util.DoubleCodec;

@RequiredArgsConstructor
public class NcERAnnotationDecoder implements AnnotationDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    short encodedScore = memoryBuffer.getInt16(index * Short.BYTES);
    Double decodedScore = doubleCodec.decodeDoubleFromShort(encodedScore, PERC_MIN, PERC_MAX);
    return new DoubleValueAnnotation(decodedScore);
  }
}
