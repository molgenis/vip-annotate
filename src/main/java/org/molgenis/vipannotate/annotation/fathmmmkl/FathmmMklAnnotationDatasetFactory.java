package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class FathmmMklAnnotationDatasetFactory {
  private final FathmmMklAnnotationDatasetDecoder annotationDatasetDecoder;

  public FathmmMklAnnotationDataset create(MemoryBuffer scoreMemoryBuffer) {
    return new FathmmMklAnnotationDataset(annotationDatasetDecoder, scoreMemoryBuffer);
  }
}
