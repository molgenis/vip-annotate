package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Annotation database:
 *
 * <ul>
 *   <li>containing annotations for genome positions
 *   <li>to annotate sequence variants
 * </ul>
 *
 * @param <T> annotation type
 */
@RequiredArgsConstructor
public class PositionAnnotationDb<T extends Annotation>
    implements AnnotationDb<SequenceVariant, T> {
  @NonNull private final AnnotationDatasetReader<T> annotationDatasetReader;

  private Partition.Key activePartitionKey;
  private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public T findAnnotations(SequenceVariant feature) {
    if (feature.getType() != SequenceVariantType.SNV) {
      return null; // TODO annotate non-SNPs
    }

    // determine partition
    Partition.Key partitionKey =
        new Partition.Key(feature.getContig(), Partition.calcBin(feature.getStart()));

    // handle partition changes
    if (!partitionKey.equals(activePartitionKey)) {
      activeAnnotationDataset = annotationDatasetReader.read(partitionKey);
      activePartitionKey = partitionKey;
    }

    int partitionStart = Partition.getPartitionStart(partitionKey, feature.getStart());
    return activeAnnotationDataset.findByIndex(partitionStart);
  }

  @Override
  public void close() {
    annotationDatasetReader.close();
  }
}
