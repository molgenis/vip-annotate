package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAnnotationDatasetReader
    implements AnnotationDatasetDecoder<CompositeAnnotation> {
  private final AnnotationDatasetDecoder<?>[] datasetReaders;

  @Override
  public AnnotationDataset<CompositeAnnotation> decode(PartitionKey partitionKey) {
    AnnotationDataset[] annotationDatasets = new AnnotationDataset[datasetReaders.length];
    for (int i = 0, length = datasetReaders.length; i < length; i++) {
      annotationDatasets[i] = datasetReaders[i].decode(partitionKey);
    }
    return new CompositeAnnotationDataset(annotationDatasets);
  }

  @Override
  public void close() {}
}
