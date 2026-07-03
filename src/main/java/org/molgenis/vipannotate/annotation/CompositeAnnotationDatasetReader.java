package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotationDatasetReader
    implements AnnotationDatasetReader<CompositeAnnotation> {
  private final ScalarAnnotationDatasetReader[] datasetReaders;

  @Override
  public AnnotationDataset<CompositeAnnotation> read(PartitionKey partitionKey) {
    AnnotationDataset<ScalarAnnotation>[] annotationDatasets =
        new ScalarAnnotationDataset[datasetReaders.length];
    for (int i = 0, length = datasetReaders.length; i < length; i++) {
      annotationDatasets[i] = datasetReaders[i].read(partitionKey);
    }
    return new CompositeAnnotationDataset(annotationDatasets);
  }

  @Override
  public void close() {}
}
