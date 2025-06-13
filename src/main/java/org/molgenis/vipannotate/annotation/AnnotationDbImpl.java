package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotationDbImpl<T> implements AnnotationDb<T> {
  @NonNull private final AnnotationIndexReader annotationIndexReader;
  @NonNull private final AnnotationDatasetReader<T> annotationDatasetReader;

  private GenomePartitionKey activeGenomePartitionKey;
  private AnnotationIndex activeAnnotationIndex;
  private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public T findAnnotations(Variant variant) {
    // determine partition
    GenomePartitionKey genomePartitionKey =
        new GenomePartitionKey(variant.contig(), GenomePartition.calcBin(variant.start()));

    // handle partition changes
    if (!genomePartitionKey.equals(activeGenomePartitionKey)) {
      activeAnnotationIndex = annotationIndexReader.read(genomePartitionKey);
      activeAnnotationDataset = null; // invalidate but defer loading until the first index hit
      activeGenomePartitionKey = genomePartitionKey;
    }

    int index = activeAnnotationIndex.findIndex(variant);

    T annotationData;
    if (index != -1) {
      if (activeAnnotationDataset == null) {
        // load annotation data on the first index hit
        activeAnnotationDataset = annotationDatasetReader.read(activeGenomePartitionKey);
      }

      annotationData = activeAnnotationDataset.findById(index);
    } else {
      annotationData = null;
    }

    return annotationData;
  }

  @Override
  public void close() {
    // FIXME annotationIndexReader and annotationDatasetReader
  }
}
