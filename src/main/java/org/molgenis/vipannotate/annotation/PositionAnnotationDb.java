package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Annotation database containing annotations for genome positions used to annotate sequence
 * variants.
 *
 * @param <T> annotation type
 */
@RequiredArgsConstructor
public class PositionAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, AnnotationCollection<T>> {
  @NonNull private final AnnotationDatasetReader<T> annotationDatasetReader;

  private Partition.Key activePartitionKey;
  private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public AnnotationCollection<T> findAnnotations(SequenceVariant feature) {
    int start = feature.getStart();
    int refLength = feature.getRefLength();
    List<T> annotations = new ArrayList<>(refLength);
    for (int i = 0; i < refLength; ++i) {
      annotations.add(findAnnotations(feature, start + i));
    }
    return new AnnotationCollection<>(annotations);
  }

  private T findAnnotations(SequenceVariant feature, int pos) {
    // determine partition
    Partition.Key partitionKey = Partition.Key.create(feature.getContig(), pos);

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      activeAnnotationDataset = annotationDatasetReader.read(partitionKey);
      activePartitionKey = partitionKey;
    }

    int partitionStart = partitionKey.getPartitionStart(feature);
    return activeAnnotationDataset.findByIndex(partitionStart);
  }

  @Override
  public void close() {
    annotationDatasetReader.close();
  }
}
