package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class PositionScoreAnnotationDatasetFactory {
  private final AnnotationDecoder<DoubleValueAnnotation> annotationDatasetDecoder;

  public PositionScoreAnnotationDataset create(MemoryBuffer scoreMemoryBuffer) {
    return new PositionScoreAnnotationDataset(annotationDatasetDecoder, scoreMemoryBuffer);
  }
}
