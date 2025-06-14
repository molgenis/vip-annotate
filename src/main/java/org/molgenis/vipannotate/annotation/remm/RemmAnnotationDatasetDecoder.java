package org.molgenis.vipannotate.annotation.remm;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;

@RequiredArgsConstructor
public class RemmAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {
  @NonNull private final RemmAnnotationDataCodec remmAnnotationDataCodec;

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    int relativeIndex = index - ((index >> 20) << 20);
    byte encodedScore = memoryBuffer.getByte(relativeIndex);
    Double decodedScore = remmAnnotationDataCodec.decode(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
