package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetFactory {
  @NonNull
  private final AnnotationDatasetDecoder<ContigPosScoreAnnotationData> annotationDatasetDecoder;

  public ContigPosScoreAnnotationDataset create(@NonNull MemoryBuffer scoreMemoryBuffer) {
    return new ContigPosScoreAnnotationDataset(annotationDatasetDecoder, scoreMemoryBuffer);
  }
}
