package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetFactory {
  private final AnnotationDatasetDecoder<DoubleValueAnnotation> annotationDatasetDecoder;

  public ContigPosScoreAnnotationDataset create(MemoryBuffer scoreMemoryBuffer) {
    return new ContigPosScoreAnnotationDataset(annotationDatasetDecoder, scoreMemoryBuffer);
  }
}
