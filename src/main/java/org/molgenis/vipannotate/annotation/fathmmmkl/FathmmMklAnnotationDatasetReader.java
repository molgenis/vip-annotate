package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.annotation.*;

@RequiredArgsConstructor
public class FathmmMklAnnotationDatasetReader
    implements AnnotationDatasetReader<FathmmMklAnnotation> {
  private final FathmmMklAnnotationDatasetFactory annotationDatasetFactory;
  private final AnnotationBlobReader scoreAnnotationBlobReader;

  @Override
  public AnnotationDataset<FathmmMklAnnotation> read(Partition.Key partitionKey) {
    MemoryBuffer scoreMemoryBuffer = scoreAnnotationBlobReader.read(partitionKey);

    AnnotationDataset<FathmmMklAnnotation> annotationDataset;
    if (scoreMemoryBuffer != null) {
      annotationDataset = annotationDatasetFactory.create(scoreMemoryBuffer);
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }

  @Override
  public void close() {
    scoreAnnotationBlobReader.close();
  }
}
