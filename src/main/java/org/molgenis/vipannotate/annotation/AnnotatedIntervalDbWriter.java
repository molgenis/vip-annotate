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
  private final AnnotatedIntervalPartitionWriter<T, U, V> annotatedIntervalPartitionWriter;

  @Override
  public void write(Iterator<V> annotatedFeatureIt) {
    List<V> reusableAnnotatedIntervals = new ArrayList<>();
    for (PartitionIterator<T, U, V> partitionIt =
            new PartitionIterator<>(annotatedFeatureIt, reusableAnnotatedIntervals);
        partitionIt.hasNext(); ) {
      Partition<T, U, V> partition = partitionIt.next();
      if (Logger.isDebugEnabled()) {
        Logger.debug(
            "processing partition %s/%d",
            partition.key().contig().getName(), partition.key().bin());
      }
      annotatedIntervalPartitionWriter.write(partition);
    }
  }
}
