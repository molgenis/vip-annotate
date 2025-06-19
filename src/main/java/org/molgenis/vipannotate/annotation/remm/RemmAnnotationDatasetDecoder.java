package org.molgenis.vipannotate.annotation.remm;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.util.Encoder;

public class RemmAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<DoubleValueAnnotation> {

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = Encoder.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }
}
