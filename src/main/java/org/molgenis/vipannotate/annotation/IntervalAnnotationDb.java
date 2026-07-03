package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;

/**
 * A database of genomic position annotations used to annotate genomic intervals.
 *
 * @param <T> annotation type
 */
@RequiredArgsConstructor
public class IntervalAnnotationDb<T extends Interval, U extends Annotation>
    implements AnnotationDb<T, U> {
  private final PartitionResolver partitionResolver;
  private final AnnotationDatasetReader<U> annotationDatasetReader;

  @Nullable private PartitionKey activePartitionKey;
  @Nullable private AnnotationDataset<@Nullable U> activeAnnotationDataset;

  @Override
  public void findAnnotations(T interval, List<U> annotations) {
    Contig contig = interval.getContig();
    for (int pos = interval.getStart(), stop = interval.getStop(); pos <= stop; ++pos) {
      U posAnnotations = findAnnotations(contig, pos);
      if (posAnnotations != null) {
        annotations.add(posAnnotations);
      }
    }
  }

  @SuppressWarnings("NullAway")
  private @Nullable U findAnnotations(Contig contig, int pos) {
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
