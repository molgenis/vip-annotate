package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IndexRangeFinderTest {
  @Test
  void findIndexesSingle() {
    assertEquals(
        new IndexRange(1, 1), IndexRangeFinder.findIndexes(new int[] {0, 1, 2, 3, 4}, 0, 5, 1));
  }

  @Test
  void findIndexesMultiple() {
    assertEquals(
        new IndexRange(1, 3), IndexRangeFinder.findIndexes(new int[] {0, 1, 1, 1, 2}, 0, 5, 1));
  }

  @Test
  void findIndexesMultipleStart() {
    assertEquals(
        new IndexRange(0, 2), IndexRangeFinder.findIndexes(new int[] {1, 1, 1, 2, 3}, 0, 5, 1));
  }

  @Test
  void findIndexesMultipleStartFromIndex() {
    assertEquals(
        new IndexRange(1, 2), IndexRangeFinder.findIndexes(new int[] {1, 1, 1, 2, 3}, 1, 5, 1));
  }

  @Test
  void findIndexesMultipleEnd() {
    assertEquals(
        new IndexRange(2, 4), IndexRangeFinder.findIndexes(new int[] {0, 1, 2, 2, 2}, 0, 5, 2));
  }

  @Test
  void findIndexesMultipleEndToIndex() {
    assertEquals(
        new IndexRange(2, 3), IndexRangeFinder.findIndexes(new int[] {0, 1, 2, 2, 2}, 0, 4, 2));
  }

  @Test
  void findIndexesNone() {
    assertNull(IndexRangeFinder.findIndexes(new int[] {0, 1, 2, 3, 4}, 0, 5, 5));
  }

  @Test
  void findIndexesNoneOutsideOfToIndex() {
    assertNull(IndexRangeFinder.findIndexes(new int[] {0, 1, 2, 3, 4}, 0, 4, 4));
  }

  @Test
  void findIndexesComparableSingle() {
    assertEquals(
        new IndexRange(1, 1), IndexRangeFinder.findIndexes(new Integer[] {0, 1, 2, 3, 4}, 0, 5, 1));
  }

  @Test
  void findIndexesComparableMultiple() {
    assertEquals(
        new IndexRange(1, 3), IndexRangeFinder.findIndexes(new Integer[] {0, 1, 1, 1, 2}, 0, 5, 1));
  }

  @Test
  void findIndexesComparableMultipleStart() {
    assertEquals(
        new IndexRange(0, 2), IndexRangeFinder.findIndexes(new Integer[] {1, 1, 1, 2, 3}, 0, 5, 1));
  }

  @Test
  void findIndexesComparableMultipleStartFromIndex() {
    assertEquals(
        new IndexRange(1, 2), IndexRangeFinder.findIndexes(new Integer[] {1, 1, 1, 2, 3}, 1, 5, 1));
  }

  @Test
  void findIndexesComparableMultipleEnd() {
    assertEquals(
        new IndexRange(2, 4), IndexRangeFinder.findIndexes(new Integer[] {0, 1, 2, 2, 2}, 0, 5, 2));
  }

  @Test
  void findIndexesComparableMultipleEndToIndex() {
    assertEquals(
        new IndexRange(2, 3), IndexRangeFinder.findIndexes(new Integer[] {0, 1, 2, 2, 2}, 0, 4, 2));
  }

  @Test
  void findIndexesComparableNone() {
    assertNull(IndexRangeFinder.findIndexes(new Integer[] {0, 1, 2, 3, 4}, 0, 5, 5));
  }

  @Test
  void findIndexesComparableNoneOutsideOfToIndex() {
    assertNull(IndexRangeFinder.findIndexes(new Integer[] {0, 1, 2, 3, 4}, 0, 4, 4));
  }
}
