package org.molgenis.vipannotate.util;

import static java.util.Collections.emptyIterator;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class FlatteningIteratorTest {
  @Test
  void hasNextAndNextWithMultipleLists() {
    Iterator<List<Integer>> listIt = List.of(List.of(1, 2), List.of(3), List.of(4, 5)).iterator();
    Iterator<Integer> it = new FlatteningIterator<>(listIt);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertEquals(3, it.next());
    assertEquals(4, it.next());
    assertEquals(5, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextAndNextMultipleListsWithEmptyLists() {
    Iterator<List<Integer>> listIt =
        List.of(List.<Integer>of(), List.of(1, 2), List.<Integer>of()).iterator();
    Iterator<Integer> it = new FlatteningIterator<>(listIt);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void testFlatteningIteratorWithAllEmptyLists() {
    Iterator<List<Integer>> listIt = List.of(List.<Integer>of(), List.<Integer>of()).iterator();
    Iterator<Integer> it = new FlatteningIterator<>(listIt);
    assertFalse(it.hasNext());
  }

  @Test
  void testFlatteningIteratorWithEmptyIterator() {
    Iterator<Integer> it = new FlatteningIterator<>(emptyIterator());
    assertFalse(it.hasNext());
  }

  @Test
  void testFlatteningIteratorThrowsException() {
    Iterator<Integer> it = new FlatteningIterator<>(emptyIterator());
    assertThrows(NoSuchElementException.class, it::next);
  }
}
