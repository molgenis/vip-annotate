package org.molgenis.vipannotate.annotation;

import java.util.Arrays;
import java.util.Collection;

public abstract class Pool<T> {
  @SuppressWarnings("unchecked")
  private T[] available = (T[]) new Object[16];

  private int size;
  private int created;

  protected abstract T create();

  public T acquire() {
    if (size == 0) {
      ensureCapacity(created + 1);
      T value = create();
      created++;
      return value;
    }

    T value = available[--size];
    available[size] = null;
    return value;
  }

  public void release(T value) {
    available[size++] = value;
  }

  public void releaseAll(Collection<T> values) {
    for (T value : values) {
      release(value);
    }
  }

  private void ensureCapacity(int required) {
    if (required > available.length) {
      int capacity = Integer.highestOneBit(required - 1) << 1;
      available = Arrays.copyOf(available, capacity);
    }
  }
}
