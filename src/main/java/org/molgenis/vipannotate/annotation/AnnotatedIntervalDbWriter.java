package org.molgenis.vipannotate.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.util.Logger;

/**
 * Writes annotated genomic intervals to a partitioned database
 *
 * @param <T> type of genomic interval
 * @param <U> type of genomic interval annotation
 * @param <V> annotated genomic interval typed by T and U
 */
@RequiredArgsConstructor
public class AnnotatedIntervalDbWriter<
        T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements AnnotatedFeatureDbWriter<T, U, V> {
  private final PartitionResolver partitionResolver;
  private final AnnotatedIntervalPartitionWriter<T, U, V> annotatedIntervalPartitionWriter;

  @Override
  public void write(Iterator<V> annotatedFeatureIt) {
    for (PartitionIterator<T, U, V> it = createPartitionIt(annotatedFeatureIt); it.hasNext(); ) {
      Partition<T, U, V> partition = it.next();
      if (Logger.isDebugEnabled()) {
        PartitionKey key = partition.key();
        Logger.debug("processing partition %s/%d", key.contig().getName(), key.bin());
      }
      annotatedIntervalPartitionWriter.write(partition);
    }
  }

  private PartitionIterator<T, U, V> createPartitionIt(Iterator<V> sourceIt) {
    List<V> reusableAnnotatedIntervals = new ArrayList<>();
    return new PartitionIterator<>(partitionResolver, sourceIt, reusableAnnotatedIntervals);
  }
}
