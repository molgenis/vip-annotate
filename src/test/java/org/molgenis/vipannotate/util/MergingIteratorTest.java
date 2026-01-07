package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class MergingIteratorTest {
  @Test
  void hasNextAndNextUnique() {
    Iterator<Integer> sourceIt1 = List.of(1, 3, 5).iterator();
    Iterator<Integer> sourceIt2 = List.of(2, 4, 6).iterator();
    MergingIterator<Integer> it = new MergingIterator<>(sourceIt1, sourceIt2, Integer::compare);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertTrue(it.hasNext());
    assertEquals(3, it.next());
    assertTrue(it.hasNext());
    assertEquals(4, it.next());
    assertTrue(it.hasNext());
    assertEquals(5, it.next());
    assertTrue(it.hasNext());
    assertEquals(6, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextAndNextDuplicates() {
    Iterator<Integer> sourceIt1 = List.of(1, 3, 5).iterator();
    Iterator<Integer> sourceIt2 = List.of(1, 3).iterator();
    MergingIterator<Integer> it = new MergingIterator<>(sourceIt1, sourceIt2, Integer::compare);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertTrue(it.hasNext());
    assertEquals(3, it.next());
    assertTrue(it.hasNext());
    assertEquals(3, it.next());
    assertTrue(it.hasNext());
    assertEquals(5, it.next());
    assertFalse(it.hasNext());
  }
}
