package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class CompositeAnnotationDatasetReader
    implements AnnotationDatasetReader<CompositeAnnotation> {
  private final AnnotationDatasetReader<?>[] datasetReaders;

  @Override
  public AnnotationDataset<CompositeAnnotation> read(PartitionKey partitionKey) {
    AnnotationDataset<? extends Annotation>[] annotationDatasets =
        new AnnotationDataset<?>[datasetReaders.length];
    for (int i = 0, length = datasetReaders.length; i < length; i++) {
      annotationDatasets[i] = datasetReaders[i].read(partitionKey);
    }
    return new CompositeAnnotationDataset(annotationDatasets);
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(datasetReaders);
  }
}
