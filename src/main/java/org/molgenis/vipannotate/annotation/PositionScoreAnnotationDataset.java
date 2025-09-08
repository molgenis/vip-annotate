package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;

@RequiredArgsConstructor
public class PositionScoreAnnotationDataset implements AnnotationDataset<DoubleValueAnnotation> {
  private final AnnotationDatasetDecoder<DoubleValueAnnotation> annotationDataSetDecoder;
  private final MemoryBuffer scoreMemoryBuffer;

  @Override
  public DoubleValueAnnotation findByIndex(int index) {
    return annotationDataSetDecoder.decode(scoreMemoryBuffer, index);
  }
}
