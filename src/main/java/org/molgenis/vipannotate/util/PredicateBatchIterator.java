package org.molgenis.vipannotate.util;

import java.util.*;
import java.util.function.BiPredicate;
import org.jspecify.annotations.Nullable;

public class PredicateBatchIterator<T extends @Nullable Object> implements Iterator<List<T>> {
  private final PushbackIterator<T> sourceIt;
  private final BiPredicate<List<T>, T> batchPredicate;
  @Nullable private List<T> reusableBatch;

  public PredicateBatchIterator(Iterator<T> sourceIt, BiPredicate<List<T>, T> batchPredicate) {
    this.sourceIt = new PushbackIterator<>(sourceIt);
    this.batchPredicate = batchPredicate;
  }

  public PredicateBatchIterator(
      Iterator<T> sourceIt, BiPredicate<List<T>, T> batchPredicate, List<T> reusableBatch) {
    this.sourceIt = new PushbackIterator<>(sourceIt);
    this.batchPredicate = batchPredicate;
    this.reusableBatch = reusableBatch;
  }

  @Override
  public boolean hasNext() {
    return sourceIt.hasNext();
  }

  /**
   * @return next non-empty batch of items
   */
  @Override
  public List<T> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    List<T> batch = getOrCreateEmptyBatch();
    while (sourceIt.hasNext()) {
      T next = sourceIt.next();
      if (batch.isEmpty() || batchPredicate.test(batch, next)) {
        batch.add(next);
      } else {
        sourceIt.pushback(next);
        break;
      }
    }
    return batch;
  }

  private List<T> getOrCreateEmptyBatch() {
    List<T> batch;
    if (reusableBatch != null) {
      reusableBatch.clear();
      batch = reusableBatch;
    } else {
      batch = new ArrayList<>();
    }
    return batch;
  }
}
