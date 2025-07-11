package org.molgenis.vipannotate.annotation;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends SequenceVariant, U extends Annotation>
    implements AnnotationDb<T, U> {
  private final AnnotationIndexReader<T> annotationIndexReader;
  private final AnnotationDatasetReader<U> annotationDatasetReader;

  private Partition.@Nullable Key activeKey;
  @Nullable private AnnotationIndex<T> activeAnnotationIndex;
  @Nullable private AnnotationDataset<U> activeAnnotationDataset;

  @Override
  public @Nullable U findAnnotations(T feature) {
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

    U annotationData;
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
