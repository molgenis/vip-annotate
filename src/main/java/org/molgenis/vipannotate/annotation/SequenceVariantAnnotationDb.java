package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, T> {
  private final AnnotationIndexReader annotationIndexReader;
  private final AnnotationDatasetReader<T> annotationDatasetReader;

  private Partition.@Nullable Key activeKey;
  @Nullable private AnnotationIndex activeAnnotationIndex;
  @Nullable private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public @Nullable T findAnnotations(SequenceVariant feature) {
    // determine partition
    Partition.Key partitionKey = Partition.createKey(feature);

    // handle partition changes
    if (!partitionKey.equals(activeKey)) {
      activeAnnotationIndex = annotationIndexReader.read(partitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activeKey = partitionKey;
    }

    @SuppressWarnings("DataFlowIssue") // false positive null warning
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
