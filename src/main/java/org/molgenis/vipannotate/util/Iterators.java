package org.molgenis.vipannotate.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Iterators {
  public static <T, R> Iterator<R> flatMap(
      Iterator<T> source, Function<? super T, ? extends Iterator<? extends R>> mapper) {

    return new Iterator<>() {
      private Iterator<? extends R> current = Collections.emptyIterator();

      @Override
      public boolean hasNext() {
        while (!current.hasNext()) {
          if (!source.hasNext()) {
            return false;
          }
          current = mapper.apply(source.next());
        }
        return true;
      }

      @Override
      public R next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return current.next();
      }
    };
  }

  public static <T, R> Iterator<R> map(
      Iterator<T> iterator, Function<? super T, ? extends R> mapper) {

    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public R next() {
        return mapper.apply(iterator.next());
      }
    };
  }
}
