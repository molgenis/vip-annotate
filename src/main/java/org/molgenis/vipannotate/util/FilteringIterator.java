package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilteringIterator<T> implements Iterator<T> {
  @NonNull private final Iterator<T> iterator;
  @NonNull private final Predicate<T> predicate;
  private T nextElement;
  private boolean nextElementSet;

  @Override
  public boolean hasNext() {
    if (nextElementSet) {
      return true;
    }
    while (iterator.hasNext()) {
      T element = iterator.next();
      if (predicate.test(element)) {
        nextElement = element;
        nextElementSet = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public T next() {
    if (!nextElementSet && !hasNext()) {
      throw new NoSuchElementException();
    }
    nextElementSet = false;
    return nextElement;
  }
}
