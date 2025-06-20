package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
  @NonNull private final AnnotatedIntervalPartitionWriter<T, U, V> annotatedIntervalPartitionWriter;

  @Override
  public void write(@NonNull Iterator<V> annotatedFeatureIt) {
    for (PartitionIterator<T, U, V> partitionIt = new PartitionIterator<>(annotatedFeatureIt);
        partitionIt.hasNext(); ) {
      annotatedIntervalPartitionWriter.write(partitionIt.next());
    }
  }
}
