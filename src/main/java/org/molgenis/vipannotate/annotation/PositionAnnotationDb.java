package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Annotation database containing annotations for genome positions used to annotate sequence
 * variants.
 *
 * @param <T> annotation type
 */
@RequiredArgsConstructor
public class PositionAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, T> {
  private final AnnotationDatasetReader<T> annotationDatasetReader;

  private Partition.@Nullable Key activePartitionKey;
  @Nullable private AnnotationDataset<@Nullable T> activeAnnotationDataset;

  @Override
  public List<T> findAnnotations(SequenceVariant feature) {
    Contig contig = feature.getContig();
    int start = feature.getStart();
    int refLength = feature.getRefLength();
    List<T> annotations = new ArrayList<>(refLength);
    for (int i = 0; i < refLength; ++i) {
      T posAnnotations = findAnnotations(contig, start + i);
      if (posAnnotations != null) {
        annotations.add(posAnnotations);
      }
    }
    return annotations;
  }

  private @Nullable T findAnnotations(Contig contig, int pos) {
    // determine partition
    Partition.Key partitionKey = Partition.createKey(contig, pos);

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      activeAnnotationDataset = annotationDatasetReader.read(partitionKey);
      activePartitionKey = partitionKey;
    }

    int partitionStart = partitionKey.getPartitionPos(pos);
    // suppress false positive null warning
    //noinspection DataFlowIssue
    return activeAnnotationDataset.findByIndex(partitionStart);
  }

  @Override
  public void close() {
    annotationDatasetReader.close();
  }
}
