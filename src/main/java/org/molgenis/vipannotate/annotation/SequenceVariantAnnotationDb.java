package org.molgenis.vipannotate.annotation;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.IndexRange;

@RequiredArgsConstructor
public class SequenceVariantAnnotationDb<T extends SequenceVariant, U extends Annotation>
    implements AnnotationDb<T, U> {
  private final AnnotationIndexReader<T> annotationIndexReader;
  private final AnnotationDatasetReader<U> annotationDatasetReader;

  private Partition.@Nullable Key activeKey;
  @Nullable private AnnotationIndex<T> activeAnnotationIndex;
  @Nullable private AnnotationDataset<U> activeAnnotationDataset;

  @Override
  public List<U> findAnnotations(T feature) {
    // determine partition
    Partition.Key partitionKey = Partition.createKey(feature);

    // handle partition changes
    if (!partitionKey.equals(activeKey)) {
      activeAnnotationIndex = annotationIndexReader.read(partitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activeKey = partitionKey;
    }

    @SuppressWarnings("DataFlowIssue")
    IndexRange indexRange = activeAnnotationIndex.findIndexes(feature);

    List<U> annotations;
    if (indexRange != null) {
      if (activeAnnotationDataset == null) {
        // load annotation data on the first index hit
        activeAnnotationDataset = annotationDatasetReader.read(activeKey);
      }

      annotations = activeAnnotationDataset.findByIndexes(indexRange);
    } else {
      annotations = Collections.emptyList();
    }

    return annotations;
  }

  @Override
  public void close() {
    annotationIndexReader.close();
    annotationDatasetReader.close();
  }
}
