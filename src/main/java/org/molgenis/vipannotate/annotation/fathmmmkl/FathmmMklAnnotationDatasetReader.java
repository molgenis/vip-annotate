package org.molgenis.vipannotate.annotation.fathmmmkl;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.serialization.MemoryBuffer;

@RequiredArgsConstructor
public class FathmmMklAnnotationDatasetReader
    implements AnnotationDatasetReader<FathmmMklAnnotation> {
  private final FathmmMklAnnotationDatasetFactory annotationDatasetFactory;
  private final AnnotationBlobReader scoreAnnotationBlobReader;

  @Override
  public AnnotationDataset<FathmmMklAnnotation> read(PartitionKey partitionKey) {
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
