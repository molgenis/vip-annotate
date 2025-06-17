package org.molgenis.vipannotate.annotation.phylop;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDatasetDecoder;
import org.molgenis.vipannotate.annotation.ContigPosScoreAnnotationData;

@RequiredArgsConstructor
public class PhyloPAnnotationDatasetDecoder
    implements AnnotationDatasetDecoder<ContigPosScoreAnnotationData> {
  @NonNull private final PhyloPAnnotationDataCodec phyloPAnnotationDataCodec;

  public ContigPosScoreAnnotationData decode(MemoryBuffer memoryBuffer, int index) {
    byte encodedScore = memoryBuffer.getByte(index);
    Double decodedScore = phyloPAnnotationDataCodec.decode(encodedScore);
    return new ContigPosScoreAnnotationData(decodedScore);
  }
}
