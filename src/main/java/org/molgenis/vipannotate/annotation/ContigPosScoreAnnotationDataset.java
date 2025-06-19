package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDataset implements AnnotationDataset<DoubleValueAnnotation> {
  @NonNull private final AnnotationDatasetDecoder<DoubleValueAnnotation> annotationDataSetDecoder;

  @NonNull private final MemoryBuffer scoreMemoryBuffer;

  @Override
  public DoubleValueAnnotation findByIndex(int index) {
    return annotationDataSetDecoder.decode(scoreMemoryBuffer, index);
  }
}
