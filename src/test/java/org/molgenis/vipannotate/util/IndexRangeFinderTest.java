package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IndexRangeFinderTest {
  @Test
  void findIndexesSingle() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {0, 1, 2, 3, 4}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 1), indexRange));
  }

  @Test
  void findIndexesMultiple() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {0, 1, 1, 1, 2}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 3), indexRange));
  }

  @Test
  void findIndexesMultipleStart() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {1, 1, 1, 2, 3}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(0, 2), indexRange));
  }

  @Test
  void findIndexesMultipleStartFromIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {1, 1, 1, 2, 3}, 1, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 2), indexRange));
  }

  @Test
  void findIndexesMultipleEnd() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {0, 1, 2, 2, 2}, 0, 5, 2, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(2, 4), indexRange));
  }

  @Test
  void findIndexesMultipleEndToIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new int[] {0, 1, 2, 2, 2}, 0, 4, 2, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(2, 3), indexRange));
  }

  @Test
  void findIndexesNone() {
    IndexRange indexRange = new IndexRange(0, 0);
    assertFalse(IndexRangeFinder.findIndexesInto(new int[] {0, 1, 2, 3, 4}, 0, 5, 5, indexRange));
  }

  @Test
  void findIndexesNoneOutsideOfToIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    assertFalse(IndexRangeFinder.findIndexesInto(new int[] {0, 1, 2, 3, 4}, 0, 4, 4, indexRange));
  }

  @Test
  void findIndexesComparableSingle() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 2, 3, 4}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 1), indexRange));
  }

  @Test
  void findIndexesComparableMultiple() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 1, 1, 2}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 3), indexRange));
  }

  @Test
  void findIndexesComparableMultipleStart() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {1, 1, 1, 2, 3}, 0, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(0, 2), indexRange));
  }

  @Test
  void findIndexesComparableMultipleStartFromIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {1, 1, 1, 2, 3}, 1, 5, 1, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(1, 2), indexRange));
  }

  @Test
  void findIndexesComparableMultipleEnd() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 2, 2, 2}, 0, 5, 2, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(2, 4), indexRange));
  }

  @Test
  void findIndexesComparableMultipleEndToIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    boolean found =
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 2, 2, 2}, 0, 4, 2, indexRange);
    assertAll(() -> assertTrue(found), () -> assertEquals(new IndexRange(2, 3), indexRange));
  }

  @Test
  void findIndexesComparableNone() {
    IndexRange indexRange = new IndexRange(0, 0);
    assertFalse(
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 2, 3, 4}, 0, 5, 5, indexRange));
  }

  @Test
  void findIndexesComparableNoneOutsideOfToIndex() {
    IndexRange indexRange = new IndexRange(0, 0);
    assertFalse(
        IndexRangeFinder.findIndexesInto(new Integer[] {0, 1, 2, 3, 4}, 0, 4, 4, indexRange));
  }
}
