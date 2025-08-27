package org.molgenis.vipannotate.util;

import java.util.Arrays;
import org.jspecify.annotations.Nullable;

public class IndexRangeFinder {
  public static @Nullable IndexRange findIndexes(int[] arr, int key) {
    int index = Arrays.binarySearch(arr, key);
    if (index < 0) {
      return null;
    }

    // find first occurrence
    int first = index;
    while (first > 0 && arr[first - 1] == key) {
      --first;
    }

    // find last occurrence
    int last = index;
    while (last < arr.length - 1 && arr[last + 1] == key) {
      --last;
    }

    return new IndexRange(first, last);
  }

  public static <T extends Comparable<? super T>> @Nullable IndexRange findIndexes(T[] arr, T key) {
    int index = Arrays.binarySearch(arr, key);
    if (index < 0) {
      return null; // not found
    }

    int first = index;
    while (first > 0 && arr[first - 1].compareTo(key) == 0) {
      first--;
    }

    int last = index;
    while (last < arr.length - 1 && arr[last + 1].compareTo(key) == 0) {
      last++;
    }

    return new IndexRange(first, last);
  }
}
