package org.molgenis.vipannotate.annotation;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlatteningIterator<T> implements Iterator<T> {
  @NonNull private final Iterator<List<T>> outerIterator;
  private Iterator<T> innerIterator;

  @Override
  public boolean hasNext() {
    while ((innerIterator == null || !innerIterator.hasNext()) && outerIterator.hasNext()) {
      innerIterator = outerIterator.next().iterator();
    }
    return innerIterator != null && innerIterator.hasNext();
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return innerIterator.next();
  }
}
