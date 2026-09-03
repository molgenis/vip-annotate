package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.ClosableUtils;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends SequenceVariant, U extends Annotation>
    implements AnnotationDb<T, U> {
  private final PartitionResolver partitionResolver;
  private final AnnotationIndexReader<T> annotationIndexReader;
  private final AnnotationDatasetDecoder<U> annotationDatasetReader;

  @Nullable private PartitionKey activePartitionKey;
  @Nullable private AnnotationIndex<T> activeAnnotationIndex;
  private boolean activeAnnotationIndexValid = false;
  @Nullable private AnnotationDataset<U> activeAnnotationDataset;

  @SuppressWarnings("NullAway")
  @Override
  public void findAnnotations(T feature, List<U> annotations) {
    PartitionKey partitionKey = partitionResolver.resolvePartitionKey(feature);

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      updateActiveAnnotationIndex(partitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activePartitionKey = partitionKey;
    }

    if (activeAnnotationIndexValid) {
      IndexRange indexRange = activeAnnotationIndex.findIndexes(feature);

      if (indexRange != null) {
        if (activeAnnotationDataset == null) {
          // load annotation data on the first index hit
          activeAnnotationDataset = annotationDatasetReader.decode(activePartitionKey);
        }

        activeAnnotationDataset.findByIndexes(indexRange, annotations);
      }
    }
  }

  private void updateActiveAnnotationIndex(PartitionKey key) {
    if (activeAnnotationIndex == null) {
      // perf: allocate an annotation index once and reuse the same instance after
      activeAnnotationIndex = annotationIndexReader.read(key);
      activeAnnotationIndexValid = (activeAnnotationIndex != null);
    } else {
      activeAnnotationIndexValid = annotationIndexReader.readInto(key, activeAnnotationIndex);
    }
  }

  @Override
  public void close() {
    ClosableUtils.closeAll(annotationIndexReader, annotationDatasetReader);
  }
}
