package org.molgenis.vipannotate.util;

import java.util.Collection;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class NumberCollections {
  public static <T extends @Nullable Object> T findMax(
      Collection<T> collection, Function<T, Double> transformFunction) {
    if (collection.isEmpty()) {
      return null;
    }

    Double max = null;
    T maxElement = null;
    for (T element : collection) {
      Double number = element != null ? transformFunction.apply(element) : null;
      if (number != null) {
        if (max == null || number > max) {
          max = number;
          maxElement = element;
        }
      }
    }
    return maxElement;
  }
}
