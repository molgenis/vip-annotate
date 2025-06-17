package org.molgenis.vipannotate.annotation.remm;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;
import org.molgenis.vipannotate.util.Encoder;

public class RemmAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = Encoder.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
