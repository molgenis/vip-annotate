package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.PushbackIterator;

/**
 * Iterator elements must be sorted by contig and within each contig by position
 *
 * @param <T>
 */
public class PartitionIterator<
        T extends Interval, U extends Annotation, V extends AnnotatedInterval<T, U>>
    implements Iterator<Partition<T, U, V>> {
  private final PushbackIterator<V> sourceIterator;
  @Nullable
  private Partition<T, U, V> reusableNextPartition;

  public PartitionIterator(Iterator<V> sourceIterator) {
    this.sourceIterator = new PushbackIterator<>(sourceIterator);
    this.reusableNextPartition = new Partition<>();
  }

  @Override
  public boolean hasNext() {
    return sourceIterator.hasNext();
  }

  @Override
  public Partition<T, U, V> next() {
    if (reusableNextPartition == null) {
      throw new NoSuchElementException();
    }
    Partition<T, U, V> currentReusableNextPartition = reusableNextPartition;
    advance();
    return currentReusableNextPartition;
  }

  private void advance() {
    if (!hasNext()) {
      reusableNextPartition = null;
      return;
    }

    reusableNextPartition.clear();

    do {
      V nextIntervalAnnotation = sourceIterator.next();

      Contig contig = nextIntervalAnnotation.getFeature().getContig();
      int bin = Partition.calcBin(nextIntervalAnnotation.getFeature().getStart());
      Partition.Key partitionKey = new Partition.Key(contig, bin);

      if (reusableNextPartition.getKey() == null) {
        reusableNextPartition.setKey(partitionKey);
      }

      if (partitionKey.equals(reusableNextPartition.getKey())) {
        reusableNextPartition.add(nextIntervalAnnotation);
      } else {
        sourceIterator.pushback(nextIntervalAnnotation);
        break;
      }
    } while (sourceIterator.hasNext());
  }
}
