package org.molgenis.vipannotate.util;

import static java.util.Collections.emptyIterator;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class PushbackIteratorTest {
  @Test
  void hasNextNext() {
    Iterator<Integer> integerIt = List.of(1, 2, 3).iterator();
    PushbackIterator<Integer> it = new PushbackIterator<>(integerIt);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertEquals(3, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void pushback() {
    Iterator<Integer> integerIt = List.of(1, 2).iterator();
    PushbackIterator<Integer> it = new PushbackIterator<>(integerIt);
    assertTrue(it.hasNext());

    Integer pushbackElement = it.next();
    assertEquals(1, pushbackElement);
    it.pushback(pushbackElement);
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void pushbackLast() {
    Iterator<Integer> integerIt = List.of(1, 2).iterator();
    PushbackIterator<Integer> it = new PushbackIterator<>(integerIt);
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    Integer pushbackElement = it.next();
    assertEquals(2, pushbackElement);
    assertFalse(it.hasNext());
    it.pushback(pushbackElement);
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void nextNotSuchElementException() {
    Iterator<String> it = new PushbackIterator<>(emptyIterator());
    assertThrows(NoSuchElementException.class, it::next);
  }
}
