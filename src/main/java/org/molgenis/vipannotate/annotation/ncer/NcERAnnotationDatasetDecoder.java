package org.molgenis.vipannotate.annotation.ncer;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;

@RequiredArgsConstructor
public class NcERAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<DoubleValueAnnotation> {
  private final NcERAnnotationDataCodec ncERAnnotationDataCodec;

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    short encodedScore = memoryBuffer.getInt16(index * Short.BYTES);
    Double decodedScore = ncERAnnotationDataCodec.decode(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }
}
