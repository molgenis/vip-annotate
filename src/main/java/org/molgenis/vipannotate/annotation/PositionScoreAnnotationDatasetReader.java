package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.serialization.MemoryBuffer;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class PositionScoreAnnotationDatasetReader
    implements AnnotationDatasetReader<DoubleValueAnnotation> {
  private final PositionScoreAnnotationDatasetFactory positionScoreAnnotationDatasetFactory;
  private final AnnotationBlobReader scoresAnnotationBlobReader;

  @Override
  public AnnotationDataset<DoubleValueAnnotation> read(PartitionKey partitionKey) {
    MemoryBuffer scoresMemoryBuffer = scoresAnnotationBlobReader.read(partitionKey);

    AnnotationDataset<DoubleValueAnnotation> annotationDataset;
    if (scoresMemoryBuffer != null) {
      annotationDataset = positionScoreAnnotationDatasetFactory.create(scoresMemoryBuffer);
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }

  @Override
  public void close() {
    ClosableUtils.close(scoresAnnotationBlobReader);
  }
}
