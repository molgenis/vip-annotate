package org.molgenis.vipannotate.annotation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vipannotate.util.SizedIterator;
import org.molgenis.vipannotate.util.TransformingIterator;

/**
 * Writes partitions of annotated genomic positions
 *
 * @param <T> type of genomic position
 * @param <U> type of genomic position annotation
 * @param <V> annotated genomic position typed by T and U
 */
@RequiredArgsConstructor
public class AnnotatedPositionPartitionWriter<
        T extends Position, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements AnnotatedIntervalPartitionWriter<T, U, V> {
  private final String annotationDataId; // TODO refactor: move to partition writer
  private final IndexedAnnotatedFeatureDatasetEncoder<U> annotationDatasetEncoder;
  private final BinaryPartitionWriter binaryPartitionWriter;

  @Override
  public void write(Partition<T, U, V> partition) {
    int maxAnnotations = partition.calcMaxPos();

    MemoryBuffer memoryBuffer =
        annotationDatasetEncoder.encode(
            createIndexedAnnotatedIntervalIterator(partition), maxAnnotations);
    binaryPartitionWriter.write(partition.key(), annotationDataId, memoryBuffer);
  }

  private SizedIterator<IndexedAnnotation<U>> createIndexedAnnotatedIntervalIterator(
      Partition<T, U, V> partition) {
    Partition.Key partitionKey = partition.key();
    List<V> annotatedIntervals = partition.annotatedIntervals();

    return new SizedIterator<>(
        new TransformingIterator<>(
            annotatedIntervals.iterator(),
            annotation -> createIndexedAnnotatedInterval(partitionKey, annotation)),
        annotatedIntervals.size());
  }

  private IndexedAnnotation<U> createIndexedAnnotatedInterval(
      Partition.Key partitionKey, V annotatedFeature) {
    int partitionStart =
        Partition.getPartitionStart(partitionKey, annotatedFeature.getFeature().getStart());
    return new IndexedAnnotation<>(partitionStart, annotatedFeature.getAnnotation());
  }
}
