package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContigPosAnnotationDb<T> implements AnnotationDb<T> {
  @NonNull private final AnnotationDatasetReader<T> annotationDatasetReader;

  private GenomePartitionKey activeGenomePartitionKey;
  private AnnotationDataset<T> activeAnnotationDataset;

  @Override
  public T findAnnotations(Variant variant) {
    // TODO annotate non-SNPs
    if (variant.getRefLength() != 1 || variant.getAltLength() != 1) {
      return null;
    }

    // determine partition
    GenomePartitionKey genomePartitionKey =
        new GenomePartitionKey(variant.contig(), GenomePartition.calcBin(variant.start()));

    // handle partition changes
    if (!genomePartitionKey.equals(activeGenomePartitionKey)) {
      activeAnnotationDataset = annotationDatasetReader.read(genomePartitionKey);
      activeGenomePartitionKey = genomePartitionKey;
    }

    int partitionStart = GenomePartition.getPartitionStart(genomePartitionKey, variant.start());
    return activeAnnotationDataset.findByIndex(partitionStart);
  }

  @Override
  public void close() {
    annotationDatasetReader.close();
  }
}
