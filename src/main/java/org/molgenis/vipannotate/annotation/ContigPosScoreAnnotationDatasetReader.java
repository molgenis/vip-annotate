package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetReader
    implements AnnotationDatasetReader<DoubleValueAnnotation> {
  @NonNull
  private final ContigPosScoreAnnotationDatasetFactory contigPosScoreAnnotationDatasetFactory;

  @NonNull private final AnnotationBlobReader scoresAnnotationBlobReader;

  @Override
  public AnnotationDataset<DoubleValueAnnotation> read(Partition.Key partitionKey) {
    MemoryBuffer scoresMemoryBuffer = scoresAnnotationBlobReader.read(partitionKey);

    AnnotationDataset<DoubleValueAnnotation> annotationDataset;
    if (scoresMemoryBuffer != null) {
      annotationDataset = contigPosScoreAnnotationDatasetFactory.create(scoresMemoryBuffer);
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }

  @Override
  public void close() {
    scoresAnnotationBlobReader.close();
  }
}
