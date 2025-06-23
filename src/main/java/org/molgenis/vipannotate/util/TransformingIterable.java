package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransformingIterable<S, T> implements Iterable<T> {
  private final Iterable<S> sourceIterable;
  private final Function<S, T> transformFunction;

  @Override
  public Iterator<T> iterator() {
    return new TransformingIterator<>(sourceIterable.iterator(), transformFunction);
  }
}
