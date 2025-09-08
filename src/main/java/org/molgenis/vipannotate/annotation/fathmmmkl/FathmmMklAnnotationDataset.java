package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.fory.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.AnnotationDataset;

@RequiredArgsConstructor
public class FathmmMklAnnotationDataset implements AnnotationDataset<FathmmMklAnnotation> {
  private final FathmmMklAnnotationDatasetDecoder annotationDataDecoder;
  private final MemoryBuffer scoreMemoryBuffer;

  @Override
  public FathmmMklAnnotation findByIndex(int index) {
    double score = annotationDataDecoder.decodeScore(scoreMemoryBuffer, index);
    return new FathmmMklAnnotation(score);
  }
}
