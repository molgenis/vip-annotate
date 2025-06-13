package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.ParameterValidation.requireNonNegative;

import java.util.Iterator;
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
  public Iterator<T> iterator() {
    return sourceIterable.iterator();
  }
}
