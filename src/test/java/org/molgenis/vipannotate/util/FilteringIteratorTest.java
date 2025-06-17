package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class FilteringIteratorTest {
  @Test
  void newWithNullIterator() {
    //noinspection DataFlowIssue
    assertThrows(
        NullPointerException.class, () -> new FilteringIterator<String>(null, s -> !s.isEmpty()));
  }

  @Test
  void hasNextNextWithNoneFiltered() {
    Iterator<String> stringIt = List.of("a", "b", "c").iterator();
    FilteringIterator<String> it = new FilteringIterator<>(stringIt, s -> !s.isEmpty());
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertEquals("c", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextNextWithSomeFiltered() {
    Iterator<String> stringIt = List.of("", "a", "").iterator();
    FilteringIterator<String> it = new FilteringIterator<>(stringIt, s -> !s.isEmpty());
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextNextWithAllFiltered() {
    Iterator<String> stringIt = List.of("", "").iterator();
    FilteringIterator<String> it = new FilteringIterator<>(stringIt, s -> !s.isEmpty());
    assertFalse(it.hasNext());
  }

  @Test
  void hasNextNextEmptyIterator() {
    Iterator<String> it = new FilteringIterator<>(Collections.emptyIterator(), s -> !s.isEmpty());
    assertFalse(it.hasNext());
  }

  @Test
  void testFilteringIteratorThrowsException() {
    Iterator<String> it = new FilteringIterator<>(Collections.emptyIterator(), s -> !s.isEmpty());
    assertThrows(NoSuchElementException.class, it::next);
  }
}
