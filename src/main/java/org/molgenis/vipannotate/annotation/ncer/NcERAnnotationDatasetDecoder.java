package org.molgenis.vipannotate.annotation.ncer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;

@RequiredArgsConstructor
public class NcERAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {
  @NonNull private final NcERAnnotationDataCodec ncERAnnotationDataCodec;

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    int relativeIndex = index - ((index >> 20) << 20);
    short encodedScore = memoryBuffer.getInt16(relativeIndex * 2); // short is 2 bytes
    Double decodedScore = ncERAnnotationDataCodec.decodeScore(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
