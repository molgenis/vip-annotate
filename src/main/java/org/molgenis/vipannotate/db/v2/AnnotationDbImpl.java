package org.molgenis.vipannotate.db.v2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.format.AnnotationDb;

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
    boolean contigChanged = !genomePartitionKey.contigEquals(activeGenomePartitionKey);
    boolean binChanged = !genomePartitionKey.binEquals(activeGenomePartitionKey);
    activeGenomePartitionKey = genomePartitionKey;

    if (contigChanged) {
      activeAnnotationIndex = annotationIndexReader.read(activeGenomePartitionKey);
      System.out.println(
          "active index: "
              + activeGenomePartitionKey.contig()
              + "/"
              + activeGenomePartitionKey.bin());
      activeAnnotationDataset = null; // invalidate
    } else if (binChanged) {
      activeAnnotationDataset = null; // invalidate
    }

    int index = activeAnnotationIndex.findIndex(variant);

    T annotationData;
    if (index != -1) {
      if (activeAnnotationDataset == null) {
        // only load annotation data on the first index hit
        activeAnnotationDataset = annotationDatasetReader.read(activeGenomePartitionKey);
        System.out.println(
            "active  data: "
                + activeGenomePartitionKey.contig()
                + "/"
                + activeGenomePartitionKey.bin());
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
