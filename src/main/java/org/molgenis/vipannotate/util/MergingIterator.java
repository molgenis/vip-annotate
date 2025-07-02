package org.molgenis.vipannotate.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jspecify.annotations.Nullable;

public class MergingIterator<T extends @Nullable Object> implements Iterator<T> {
  private final PushbackIterator<T> sourceIt1;
  private final PushbackIterator<T> sourceIt2;
  private final Comparator<T> comparator;

  public MergingIterator(Iterator<T> sourceIt1, Iterator<T> sourceIt2, Comparator<T> comparator) {
    this.sourceIt1 = new PushbackIterator<>(sourceIt1);
    this.sourceIt2 = new PushbackIterator<>(sourceIt2);
    this.comparator = comparator;
  }

  @Override
  public boolean hasNext() {
    return sourceIt1.hasNext() || sourceIt2.hasNext();
  }

  @Override
  public T next() {
    boolean hasNext1 = sourceIt1.hasNext();
    boolean hasNext2 = sourceIt2.hasNext();
    if (!hasNext1 && !hasNext2) throw new NoSuchElementException();

    T next;
    if (hasNext1 && hasNext2) {
      T next1 = sourceIt1.next();
      T next2 = sourceIt2.next();

      if (comparator.compare(next1, next2) <= 0) {
        next = next1;
        sourceIt2.pushback(next2);
      } else {
        next = next2;
        sourceIt1.pushback(next1);
      }
    } else if (hasNext1) {
      next = sourceIt1.next();
    } else {
      next = sourceIt2.next();
    }

    return next;
  }
}
