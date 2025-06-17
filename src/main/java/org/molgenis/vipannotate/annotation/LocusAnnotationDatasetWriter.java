package org.molgenis.vipannotate.annotation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterable;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

@RequiredArgsConstructor
public class LocusAnnotationDatasetWriter<T extends LocusAnnotation<U>, U>
    implements AnnotationDatasetWriter<T> {
  @NonNull private final String annotationDataId;

  @NonNull
  private final AnnotationDatasetEncoder<IndexedLocusAnnotation<T, U>> annotationDatasetEncoder;

  @NonNull private final GenomePartitionDataWriter genomePartitionDataWriter;

  @Override
  public void write(GenomePartitionKey genomePartitionKey, SizedIterable<T> annotations) {
    MemoryBuffer memoryBuffer =
        annotationDatasetEncoder.encode(
            new SizedIterator<>(
                new TransformingIterator<>(
                    annotations.iterator(), annotation -> map(genomePartitionKey, annotation)),
                annotations.getSize()));
    genomePartitionDataWriter.write(genomePartitionKey, annotationDataId, memoryBuffer);
  }

  private IndexedLocusAnnotation<T, U> map(GenomePartitionKey genomePartitionKey, T annotation) {
    int partitionStart = GenomePartition.getPartitionStart(genomePartitionKey, annotation.start());
    return new IndexedLocusAnnotation<>(partitionStart, annotation);
  }
}
