package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.SizedIterable;

@RequiredArgsConstructor
public class NonIndexedAnnotationDbWriter<T extends LocusAnnotation<U>, U> {
  @NonNull private final AnnotationDatasetWriter<T> annotationDatasetWriter;

  public void create(Iterator<T> annotationIterator) {
    for (ReusableGenomePartitionIterator<T, U> reusableGenomePartitionIterator =
            new ReusableGenomePartitionIterator<>(annotationIterator);
        reusableGenomePartitionIterator.hasNext(); ) {
      process(reusableGenomePartitionIterator.next());
    }
  }

  private void process(GenomePartition<T, U> genomePartition) {
    List<T> annotationList = genomePartition.getLocusAnnotationList();
    annotationDatasetWriter.write(
        genomePartition.getGenomePartitionKey(),
        new SizedIterable<>(annotationList,
            annotationList.size()));
  }
}
