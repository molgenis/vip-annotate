package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, T> {
  @NonNull private final AnnotationIndexReader annotationIndexReader;
  @NonNull private final AnnotationDatasetReader<T> annotationDatasetReader;

  private Partition.Key activeKey;
  private AnnotationIndex activeAnnotationIndex;
  private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public T findAnnotations(SequenceVariant feature) {
    // determine partition
    Partition.Key partitionKey =
        new Partition.Key(feature.getContig(), Partition.calcBin(feature.getStart()));

    // handle partition changes
    if (!partitionKey.equals(activeKey)) {
      activeAnnotationIndex = annotationIndexReader.read(partitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activeKey = partitionKey;
    }

    int index = activeAnnotationIndex.findIndex(feature);

    T annotationData;
    if (index != -1) {
      if (activeAnnotationDataset == null) {
        // load annotation data on the first index hit
        activeAnnotationDataset = annotationDatasetReader.read(activeKey);
      }

      annotationData = activeAnnotationDataset.findByIndex(index);
    } else {
      annotationData = null;
    }

    return annotationData;
  }

  @Override
  public void close() {
    annotationIndexReader.close();
    annotationDatasetReader.close();
  }
}
