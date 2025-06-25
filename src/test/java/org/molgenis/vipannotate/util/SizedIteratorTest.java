package org.molgenis.vipannotate.util;

import static java.util.Collections.emptyIterator;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SizedIteratorTest {
  @Test
  void newWithNegativateNumber() {
    assertThrows(
        IllegalArgumentException.class, () -> new SizedIterator<String>(emptyIterator(), -1));
  }

  @Test
  void iterator() {
    List<String> stringList = List.of("a", "b", "c");
    SizedIterator<String> it = new SizedIterator<>(stringList.iterator(), stringList.size());
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertEquals("c", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void size() {
    List<String> stringList = List.of("a", "b", "c");
    SizedIterator<String> it = new SizedIterator<>(stringList.iterator(), stringList.size());
    assertEquals(3, it.getSize());
  }
}
