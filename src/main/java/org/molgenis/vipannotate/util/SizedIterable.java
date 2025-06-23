package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.Numbers.requireNonNegative;

import lombok.Getter;

public class SizedIterable<T> implements Iterable<T> {
  private final Iterable<T> sourceIterable;
  @Getter private final int size;

  public SizedIterable(Iterable<T> sourceIterable, int size) {
    this.sourceIterable = sourceIterable;
    this.size = requireNonNegative(size);
  }

  @Override
  public SizedIterator<T> iterator() {
    return new SizedIterator<>(sourceIterable.iterator(), size);
  }
}
