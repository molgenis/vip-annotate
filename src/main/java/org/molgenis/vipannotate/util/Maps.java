package org.molgenis.vipannotate.util;

import static java.lang.Math.ceil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.jspecify.annotations.Nullable;

public class Maps {
  private static final double DEFAULT_LOAD_FACTOR = 0.75;

  private Maps() {}

  public static <K extends @Nullable Object, V extends @Nullable Object>
      HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return new HashMap<>(capacity(expectedSize));
  }

  public static <K extends @Nullable Object, V extends @Nullable Object>
      LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return new LinkedHashMap<>(capacity(expectedSize));
  }

  private static int capacity(int expectedSize) {
    Numbers.validateNonNegative(expectedSize);
    if (expectedSize < 3) {
      return expectedSize + 1;
    }
    return (int) ceil(expectedSize / DEFAULT_LOAD_FACTOR);
  }
}
