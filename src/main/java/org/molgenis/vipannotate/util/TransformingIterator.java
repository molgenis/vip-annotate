package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class TransformingIterator<S extends @Nullable Object, T extends @Nullable Object>
    implements Iterator<T> {
  private final Iterator<S> sourceIterator;
  private final Function<S, T> transformFunction;

  @Override
  public boolean hasNext() {
    return sourceIterator.hasNext();
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    S element = sourceIterator.next();
    return transformFunction.apply(element);
  }
}
