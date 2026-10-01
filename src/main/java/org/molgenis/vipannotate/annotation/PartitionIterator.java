package org.molgenis.vipannotate.annotation;

import java.util.*;
import org.molgenis.vipannotate.util.PredicateBatchIterator;

/**
 * Transforms an iterator of annotated genomic intervals to an iterator of partitions that group
 * consecutive intervals with the same key.
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
public class PartitionIterator<
        T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements Iterator<Partition<T, U, V>> {
  private final PredicateBatchIterator<V> sourceIterator;
  private final PartitionResolver partitionResolver;

  /**
   * Creates a new partition iterator
   *
   * @param partitionResolver determines how to partition
   * @param sourceIterator iterator of annotated genomic intervals
   */
  public PartitionIterator(PartitionResolver partitionResolver, Iterator<V> sourceIterator) {
    this.partitionResolver = partitionResolver;
    this.sourceIterator = new PredicateBatchIterator<>(sourceIterator, this::test);
  }

  /**
   * Creates a new partition iterator, reuses the same list to create partitions. Useful when
   * performance matters, use it with care.
   *
   * @param partitionResolver determines how to partition
   * @param sourceIterator iterator of annotated genomic intervals
   * @param reusableAnnotatedIntervalList reusable list of annotated genomic intervals
   */
  public PartitionIterator(
      PartitionResolver partitionResolver,
      Iterator<V> sourceIterator,
      List<V> reusableAnnotatedIntervalList) {
    this.partitionResolver = partitionResolver;
    this.sourceIterator =
        new PredicateBatchIterator<>(sourceIterator, this::test, reusableAnnotatedIntervalList);
  }

  @Override
  public boolean hasNext() {
    return sourceIterator.hasNext();
  }

  @Override
  public Partition<T, U, V> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    List<V> nextBatch = sourceIterator.next();
    PartitionKey key = partitionResolver.resolvePartitionKey(nextBatch.getFirst());
    return new Partition<>(key, nextBatch);
  }

  private boolean test(List<V> currentBatch, V newAnnotatedInterval) {
    PartitionKey batchKey = partitionResolver.resolvePartitionKey(currentBatch.getFirst());
    PartitionKey newKey = partitionResolver.resolvePartitionKey(newAnnotatedInterval);
    return batchKey.equals(newKey);
  }
}
