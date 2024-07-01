package org.molgenis.vipannotate.util;

import static java.util.Collections.emptyIterator;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class TransformingIteratorTest {
  @Test
  void hasNextNext() {
    Iterator<String> stringIt = List.of("1", "2", "3").iterator();
    Iterator<Integer> it = new TransformingIterator<>(stringIt, Integer::parseInt);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertEquals(3, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextEmptyIterator() {
    Iterator<Integer> it =
        new TransformingIterator<String, Integer>(emptyIterator(), Integer::parseInt);
    assertFalse(it.hasNext());
  }

  @Test
  void testFilteringIteratorThrowsException() {
    Iterator<Integer> it =
        new TransformingIterator<String, Integer>(emptyIterator(), Integer::parseInt);
    assertThrows(NoSuchElementException.class, it::next);
  }
}
