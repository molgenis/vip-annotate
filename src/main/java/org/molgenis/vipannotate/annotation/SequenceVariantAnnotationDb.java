package org.molgenis.vipannotate.annotation;

import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends SequenceVariant, U extends Annotation>
    implements AnnotationDb<T, U> {
  private final PartitionResolver partitionResolver;
  private final AnnotationIndexReader<T> annotationIndexReader;
  private final AnnotationDatasetReader<U> annotationDatasetReader;
  private final Predicate<T> canAnnotate;

  @Nullable private PartitionKey activePartitionKey;
  @Nullable private AnnotationIndex<T> activeAnnotationIndex;
  @Nullable private AnnotationDataset<U> activeAnnotationDataset;

  @SuppressWarnings({"DataFlowIssue", "NullAway"})
  @Override
  public void findAnnotations(T feature, List<U> annotations) {
    if (!canAnnotate.test(feature)) {
      return;
    }

    PartitionKey partitionKey = partitionResolver.resolvePartitionKey(feature);

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      updateActiveAnnotationIndex(partitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activePartitionKey = partitionKey;
    }

    IndexRange indexRange = activeAnnotationIndex.findIndexes(feature);

    if (indexRange != null) {
      if (activeAnnotationDataset == null) {
        // load annotation data on the first index hit
        activeAnnotationDataset = annotationDatasetReader.read(activePartitionKey);
      }

      activeAnnotationDataset.findByIndexes(indexRange, annotations);
    }
  }

  private void updateActiveAnnotationIndex(PartitionKey partitionKey) {
    if (activeAnnotationIndex == null) {
      activeAnnotationIndex = annotationIndexReader.read(partitionKey);
    } else {
      annotationIndexReader.readInto(partitionKey, activeAnnotationIndex);
    }
  }

  @Override
  public void close() {
    annotationIndexReader.close();
    annotationDatasetReader.close();
  }
}
