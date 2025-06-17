package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransformingIterable<S, T> implements Iterable<T> {
  @NonNull private final Iterable<S> sourceIterable;
  @NonNull private final Function<S, T> transformFunction;

  // do not add @lombok.NonNull to return value
  @SuppressWarnings("NullableProblems")
  @Override
  public Iterator<T> iterator() {
    return new TransformingIterator<>(sourceIterable.iterator(), transformFunction);
  }
}
