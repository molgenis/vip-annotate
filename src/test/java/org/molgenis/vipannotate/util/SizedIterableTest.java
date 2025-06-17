package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SizedIterableTest {
  @Test
  void newWithNullIterator() {
    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> new SizedIterable<String>(null, 1));
  }

  @Test
  void newWithNegativateNumber() {
    assertThrows(IllegalArgumentException.class, () -> new SizedIterable<String>(List.of(), -1));
  }

  @Test
  void iterator() {
    List<String> stringList = List.of("a", "b", "c");
    SizedIterator<String> it = new SizedIterable<>(stringList, stringList.size()).iterator();
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertEquals("c", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void size() {
    List<String> stringList = List.of("a", "b", "c");
    SizedIterable<String> iterable = new SizedIterable<>(stringList, stringList.size());
    assertEquals(3, iterable.getSize());
  }
}
