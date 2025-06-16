package org.molgenis.vipannotate.annotation.remm;

import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;
import org.molgenis.vipannotate.annotation.GenomePartition;
import org.molgenis.vipannotate.util.Encoder;

public class RemmAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    int relativeIndex = GenomePartition.calcPosInBin(index);
    byte encodedScore = memoryBuffer.getByte(relativeIndex);
    Double decodedScore = Encoder.decodeDoubleUnitIntervalFromByte(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
