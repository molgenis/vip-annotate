package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransformingIterable<S, T> implements Iterable<T> {
  @NonNull private final Iterable<S> sourceIterable;
  @NonNull private final Function<S, T> transformFunction;

  @Override
  public Iterator<T> iterator() {
    return new TransformingIterator<>(sourceIterable.iterator(), transformFunction);
  }
}
