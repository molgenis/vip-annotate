package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;

/**
 * Annotation database containing annotations for genome positions used to annotate sequence
 * variants.
 *
 * @param <T> annotation type
 */
@RequiredArgsConstructor
public class PositionAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, T> {
  private final PartitionResolver partitionResolver;
  private final AnnotationDatasetReader<T> annotationDatasetReader;
  private final Predicate<SequenceVariant> canAnnotate;

  @Nullable private PartitionKey activePartitionKey;
  @Nullable private AnnotationDataset<@Nullable T> activeAnnotationDataset;

  @SuppressWarnings("NullAway")
  @Override
  public void findAnnotations(SequenceVariant feature, List<T> annotations) {
    if (!canAnnotate.test(feature)) {
      return;
    }

    Contig contig = feature.getContig();
    int start = feature.getStart();
    int refLength = feature.getRefLength();

    if (refLength == 1) {
      T posAnnotations = findAnnotations(contig, start);
      if (posAnnotations != null) {
        annotations.add(posAnnotations);
      }
    } else {
      for (int i = 0; i < refLength; ++i) {
        T posAnnotations = findAnnotations(contig, start + i);
        if (posAnnotations != null) {
          annotations.add(posAnnotations);
        }
      }
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Nullable
  private T findAnnotations(Contig contig, int pos) {
    PartitionKey partitionKey = partitionResolver.resolvePartitionKey(contig, pos);

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      activeAnnotationDataset = annotationDatasetReader.read(partitionKey);
      activePartitionKey = partitionKey;
    }

    int partitionStart = partitionResolver.getPartitionPos(pos);
    return activeAnnotationDataset.findByIndex(partitionStart);
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDatasetReader);
  }
}
