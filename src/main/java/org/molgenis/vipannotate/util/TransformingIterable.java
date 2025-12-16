package org.molgenis.vipannotate.util;

import java.util.Iterator;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class TransformingIterable<S extends @Nullable Object, T extends @Nullable Object>
    implements Iterable<T> {
  private final Iterable<S> sourceIterable;
  private final Function<S, T> transformFunction;

  @Override
  public Iterator<T> iterator() {
    return new TransformingIterator<>(sourceIterable.iterator(), transformFunction);
  }
}
