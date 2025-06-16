package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.ParameterValidation.requireNonNegative;

import lombok.Getter;
import lombok.NonNull;

public class SizedIterable<T> implements Iterable<T> {
  @NonNull private final Iterable<T> sourceIterable;
  @Getter private final int size;

  public SizedIterable(@NonNull Iterable<T> sourceIterable, int size) {
    this.sourceIterable = sourceIterable;
    this.size = requireNonNegative(size);
  }

  @Override
  public @NonNull SizedIterator<T> iterator() {
    return new SizedIterator<>(sourceIterable.iterator(), size);
  }
}
