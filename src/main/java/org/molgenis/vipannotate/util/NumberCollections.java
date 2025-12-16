package org.molgenis.vipannotate.util;

import java.util.Collection;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class NumberCollections {
  public static <T extends @Nullable Object, U extends @Nullable Number & Comparable<U>>
      @Nullable T findMax(Collection<T> collection, Function<T, U> transformFunction) {
    if (collection.isEmpty()) {
      return null;
    }

    U max = null;
    T maxElement = null;
    for (T element : collection) {
      U number = element != null ? transformFunction.apply(element) : null;
      if (number != null) {
        if (max == null || number.compareTo(max) > 0) {
          max = number;
          maxElement = element;
        }
      }
    }
    return maxElement;
  }
}
