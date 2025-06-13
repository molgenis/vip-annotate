package org.molgenis.vipannotate.db.v2;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.NonNull;
import org.molgenis.vipannotate.util.PushbackIterator;

/**
 * Iterator elements must be sorted by contig and within each contig by position
 *
 * @param <T>
 */
public class ReusableGenomePartitionIterator<T> implements Iterator<GenomePartition<T>> {
  private final PushbackIterator<VariantAnnotation<T>> sourceIterator;
  private GenomePartition<T> reusableNextPartition;

  public ReusableGenomePartitionIterator(@NonNull Iterator<VariantAnnotation<T>> sourceIterator) {
    this.sourceIterator = new PushbackIterator<>(sourceIterator);
    this.reusableNextPartition = new GenomePartition<>();
    advance();
  }

  @Override
  public boolean hasNext() {
    return sourceIterator.hasNext();
  }

  @Override
  public GenomePartition<T> next() {
    if (reusableNextPartition == null) {
      throw new NoSuchElementException();
    }
    GenomePartition<T> currentReusableNextPartition = reusableNextPartition;
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
      VariantAnnotation<T> nextVariantAnnotation = sourceIterator.next();

      String contig = nextVariantAnnotation.variant().contig();
      int bin = GenomePartition.calcBin(nextVariantAnnotation.variant().start());
      GenomePartitionKey genomePartitionKey = new GenomePartitionKey(contig, bin);

      if (reusableNextPartition.getGenomePartitionKey() == null) {
        reusableNextPartition.setGenomePartitionKey(genomePartitionKey);
      }

      if (genomePartitionKey.equals(reusableNextPartition.getGenomePartitionKey())) {
        reusableNextPartition.add(nextVariantAnnotation);
      } else {
        sourceIterator.pushback(nextVariantAnnotation);
        break;
      }
    } while (sourceIterator.hasNext());
  }
}
