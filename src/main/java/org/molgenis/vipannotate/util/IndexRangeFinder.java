package org.molgenis.vipannotate.util;

import java.util.Arrays;

public class IndexRangeFinder {
  /**
   * Same as {@link Arrays#binarySearch(int[], int)} but finds all indices for multiple elements
   * with the specified value.
   *
   * @param arr the array to be searched, must be sorted prior to making this call
   * @param fromIndex the index of the first element (inclusive) to be searched
   * @param toIndex the index of the last element (exclusive) to be searched
   * @param key the value to be searched for
   * @return index range of the search key or <code>null</code> if it is not contained in the array
   */
  public static boolean findIndexesInto(
      int[] arr, int fromIndex, int toIndex, int key, IndexRange indexRange) {
    int index = Arrays.binarySearch(arr, fromIndex, toIndex, key);
    if (index < 0) {
      return false;
    }

    // find first occurrence
    int first = index;
    while (first > fromIndex && arr[first - 1] == key) {
      --first;
    }

    // find last occurrence
    int last = index;
    while (last < toIndex - 1 && arr[last + 1] == key) {
      ++last;
    }

    indexRange.reset(first, last);
    return true;
  }

  /**
   * Same as {@link Arrays#binarySearch(Object[], Object)} but finds all indices for multiple
   * elements with the specified value.
   *
   * @param arr the array to be searched, must be sorted prior to making this call
   * @param fromIndex the index of the first element (inclusive) to be searched
   * @param toIndex the index of the last element (exclusive) to be searched
   * @param key the value to be searched for
   * @return index range of the search key or <code>null</code> if it is not contained in the array
   */
  public static <T extends Comparable<? super T>> boolean findIndexesInto(
      T[] arr, int fromIndex, int toIndex, T key, IndexRange indexRange) {
    int index = Arrays.binarySearch(arr, fromIndex, toIndex, key);
    if (index < 0) {
      return false; // not found
    }

    int first = index;
    while (first > fromIndex && arr[first - 1].compareTo(key) == 0) {
      first--;
    }

    int last = index;
    while (last < toIndex - 1 && arr[last + 1].compareTo(key) == 0) {
      last++;
    }

    indexRange.reset(first, last);
    return true;
  }
}
