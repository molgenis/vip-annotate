package org.molgenis.vipannotate.util;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vipannotate.util.ParameterValidation.requirePositive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Batch iterator that reuses the list returned by <code>next()</code> on later calls.
 *
 * @param <T> the type of elements returned by this iterator
 */
public class ReusableBatchIterator<T> implements Iterator<List<T>> {
  private final Iterator<T> source;
  private final int batchSize;
  private List<T> reusableBatch;

  public ReusableBatchIterator(Iterator<T> source, int batchSize) {
    this.source = requireNonNull(source);
    this.batchSize = requirePositive(batchSize);
  }

  @Override
  public boolean hasNext() {
    return source.hasNext();
  }

  @Override
  public List<T> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    // lazy init
    if (reusableBatch == null) {
      reusableBatch = new ArrayList<>(batchSize);
    } else {
      reusableBatch.clear();
    }

    int count = 0;
    while (count < batchSize && source.hasNext()) {
      reusableBatch.add(source.next());
      count++;
    }

    return reusableBatch;
  }
}
