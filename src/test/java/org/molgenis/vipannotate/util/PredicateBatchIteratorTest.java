package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class PredicateBatchIteratorTest {
  @Test
  void hasNextAndNext() {
    List<Integer> integerList = List.of(1, 2, 3, 4, 5);
    PredicateBatchIterator<Integer> integerIt =
        new PredicateBatchIterator<>(
            integerList.iterator(), (currentBatch, integer) -> integer % 2 == 0);
    assertTrue(integerIt.hasNext());
    assertEquals(List.of(1, 2), integerIt.next());
    assertTrue(integerIt.hasNext());
    assertEquals(List.of(3, 4), integerIt.next());
    assertTrue(integerIt.hasNext());
    assertEquals(List.of(5), integerIt.next());
    assertFalse(integerIt.hasNext());
  }

  @Test
  void hasNextAndNextReusable() {
    List<Integer> reusableBatch = new ArrayList<>();
    List<Integer> integerList = List.of(1, 2, 3, 4, 5);
    PredicateBatchIterator<Integer> integerIt =
        new PredicateBatchIterator<>(
            integerList.iterator(), (currentBatch, integer) -> integer % 2 == 0, reusableBatch);
    assertTrue(integerIt.hasNext());
    List<Integer> actualBatch0 = integerIt.next();
    assertEquals(List.of(1, 2), actualBatch0);
    assertSame(reusableBatch, actualBatch0);

    assertTrue(integerIt.hasNext());
    List<Integer> actualBatch1 = integerIt.next();
    assertEquals(List.of(3, 4), actualBatch1);
    assertSame(reusableBatch, actualBatch1);

    assertTrue(integerIt.hasNext());
    List<Integer> actualBatch2 = integerIt.next();
    assertEquals(List.of(5), actualBatch2);
    assertSame(reusableBatch, actualBatch2);

    assertFalse(integerIt.hasNext());
  }

  @Test
  void nextThrowsNoSuchElementException() {
    PredicateBatchIterator<Integer> integerIt =
        new PredicateBatchIterator<>(Collections.emptyIterator(), (currentBatch, integer) -> true);
    assertThrows(NoSuchElementException.class, integerIt::next);
  }
}
