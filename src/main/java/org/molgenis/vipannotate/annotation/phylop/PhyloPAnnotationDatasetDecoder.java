package org.molgenis.vipannotate.annotation.phylop;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;

@RequiredArgsConstructor
public class PhyloPAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<DoubleValueAnnotation> {
  private final PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = phyloPAnnotationDataCodec.decode(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }
}
