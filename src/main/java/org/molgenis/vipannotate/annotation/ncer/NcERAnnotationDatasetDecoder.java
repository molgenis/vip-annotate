package org.molgenis.vipannotate.annotation.ncer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;
import org.molgenis.vipannotate.annotation.GenomePartition;

@RequiredArgsConstructor
public class NcERAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {
  @NonNull private final NcERAnnotationDataCodec ncERAnnotationDataCodec;

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    int relativeIndex = GenomePartition.calcPosInBin(index);
    short encodedScore = memoryBuffer.getInt16(relativeIndex * Short.BYTES);
    Double decodedScore = ncERAnnotationDataCodec.decode(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
