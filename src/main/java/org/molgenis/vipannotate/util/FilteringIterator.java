package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

// false positive: [NullAway] initializer method does not guarantee @NonNull field nextElement
@SuppressWarnings("NullAway")
@RequiredArgsConstructor
public class FilteringIterator<T extends @Nullable Object> implements Iterator<T> {
  private final Iterator<T> iterator;
  private final Predicate<T> predicate;
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
