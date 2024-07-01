package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class FlatteningIterator<T extends @Nullable Object> implements Iterator<T> {
  private final Iterator<List<T>> outerIterator;
  @Nullable private Iterator<T> innerIterator;

  @Override
  public boolean hasNext() {
    while ((innerIterator == null || !innerIterator.hasNext()) && outerIterator.hasNext()) {
      innerIterator = outerIterator.next().iterator();
    }
    return innerIterator != null && innerIterator.hasNext();
  }

  @SuppressWarnings({"DataFlowIssue", "NullAway"})
  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return innerIterator.next();
  }
}
