package org.molgenis.vipannotate.annotation.phylop;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;
import org.molgenis.vipannotate.annotation.GenomePartition;

@RequiredArgsConstructor
public class PhyloPAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {
  @NonNull private final PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    int relativeIndex = GenomePartition.calcPosInBin(index);
    byte encodedScore = memoryBuffer.getByte(relativeIndex);
    Double decodedScore = phyloPAnnotationDataCodec.decode(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
