package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransformingIterableTest {
  @Test
  void hasNextNext() {
    List<String> stringList = List.of("1", "2", "3");
    Iterator<Integer> it = new TransformingIterable<>(stringList, Integer::parseInt).iterator();
    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertEquals(2, it.next());
    assertEquals(3, it.next());
    assertFalse(it.hasNext());
  }
}
