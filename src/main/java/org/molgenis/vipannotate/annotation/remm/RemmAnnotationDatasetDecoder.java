package org.molgenis.vipannotate.annotation.remm;

import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.DoubleValueAnnotation;
import org.molgenis.vipannotate.util.DoubleCodec;

public class RemmAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<DoubleValueAnnotation> {
  private final DoubleCodec doubleCodec;

  public RemmAnnotationDatasetDecoder() {
    this(new DoubleCodec());
  }

  RemmAnnotationDatasetDecoder(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public DoubleValueAnnotation decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = doubleCodec.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new DoubleValueAnnotation(decodedScore);
  }
}
