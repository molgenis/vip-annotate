package org.molgenis.vipannotate.util;

import java.util.*;

public class PushbackIterator<E> implements Iterator<E> {
  private final Iterator<E> iterator;
  private final Deque<E> buffer;

  public PushbackIterator(Iterator<E> iterator) {
    this.iterator = iterator;
    this.buffer = new ArrayDeque<>(1);
  }

  @Override
  public boolean hasNext() {
    return !buffer.isEmpty() || iterator.hasNext();
  }

  @Override
  public E next() {
    if (!buffer.isEmpty()) {
      return buffer.pop();
    }
    if (iterator.hasNext()) {
      return iterator.next();
    }
    throw new NoSuchElementException();
  }

  public void pushback(E item) {
    buffer.push(item);
  }
}
