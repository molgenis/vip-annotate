package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;

@RequiredArgsConstructor
public class ContigPosScoreAnnotationDatasetReader
    implements AnnotationDatasetReader<ContigPosScoreAnnotationData> {
  @NonNull
  private final ContigPosScoreAnnotationDatasetFactory contigPosScoreAnnotationDatasetFactory;

  @NonNull private final AnnotationBlobReader scoresAnnotationBlobReader;

  @Override
  public AnnotationDataset<ContigPosScoreAnnotationData> read(GenomePartitionKey key) {
    MemoryBuffer scoresMemoryBuffer = scoresAnnotationBlobReader.read(key);

    AnnotationDataset<ContigPosScoreAnnotationData> annotationDataset;
    if (scoresMemoryBuffer != null) {
      annotationDataset = contigPosScoreAnnotationDatasetFactory.create(scoresMemoryBuffer);
    } else {
      annotationDataset = EmptyAnnotationDataset.getInstance();
    }
    return annotationDataset;
  }
}
