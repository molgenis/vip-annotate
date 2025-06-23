package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.Numbers.*;

import java.util.Iterator;
import lombok.Getter;

public class SizedIterator<T> implements Iterator<T> {
  private final Iterator<T> iterator;
  @Getter private final int size;

  public SizedIterator(Iterator<T> iterator, int size) {
    this.iterator = iterator;
    this.size = requireNonNegative(size);
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return iterator.next();
  }
}
