package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class PositionScoreAnnotationDatasetFactory {
  private final AnnotationDatasetDecoder<DoubleValueAnnotation> annotationDatasetDecoder;

  public PositionScoreAnnotationDataset create(MemoryBuffer scoreMemoryBuffer) {
    return new PositionScoreAnnotationDataset(annotationDatasetDecoder, scoreMemoryBuffer);
  }
}
