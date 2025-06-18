package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.NonNull;
import org.molgenis.vipannotate.util.PushbackIterator;

/**
 * Iterator elements must be sorted by contig and within each contig by position
 *
 * @param <T>
 */
public class ReusableGenomePartitionIterator<T extends IntervalAnnotation<U>, U>
    implements Iterator<GenomePartition<T, U>> {
  private final PushbackIterator<T> sourceIterator;
  private GenomePartition<T, U> reusableNextPartition;

  public ReusableGenomePartitionIterator(@NonNull Iterator<T> sourceIterator) {
    this.sourceIterator = new PushbackIterator<>(sourceIterator);
    this.reusableNextPartition = new GenomePartition<>();
  }

  @Override
  public boolean hasNext() {
    return sourceIterator.hasNext();
  }

  @Override
  public GenomePartition<T, U> next() {
    if (reusableNextPartition == null) {
      throw new NoSuchElementException();
    }
    GenomePartition<T, U> currentReusableNextPartition = reusableNextPartition;
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
      T nextIntervalAnnotation = sourceIterator.next();

      String contig = nextIntervalAnnotation.contig();
      int bin = GenomePartition.calcBin(nextIntervalAnnotation.start());
      GenomePartitionKey genomePartitionKey = new GenomePartitionKey(contig, bin);

      if (reusableNextPartition.getGenomePartitionKey() == null) {
        reusableNextPartition.setGenomePartitionKey(genomePartitionKey);
      }

      if (genomePartitionKey.equals(reusableNextPartition.getGenomePartitionKey())) {
        reusableNextPartition.add(nextIntervalAnnotation);
      } else {
        sourceIterator.pushback(nextIntervalAnnotation);
        break;
      }
    } while (sourceIterator.hasNext());
  }
}
