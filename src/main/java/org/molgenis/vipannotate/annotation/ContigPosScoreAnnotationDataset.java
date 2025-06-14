package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDataset
    implements AnnotationDataset<ContigPosScoreAnnotationData> {
  @NonNull
  private final AnnotationDatasetDecoder<ContigPosScoreAnnotationData> annotationDataSetDecoder;

  @NonNull private final MemoryBuffer scoreMemoryBuffer;

  @Override
  public ContigPosScoreAnnotationData findByIndex(int index) {
    return annotationDataSetDecoder.decode(scoreMemoryBuffer, index);
  }
}
