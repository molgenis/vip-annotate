package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class NumberCollectionsTest {

  @Test
  void findMaxListInteger() {
    List<Integer> integerList0 = List.of(-1);
    List<Integer> integerList1 = List.of(1);
    List<Integer> integerList2 = List.of(0);
    List<List<Integer>> integerLists = Arrays.asList(integerList0, integerList1, integerList2);
    assertEquals(integerList1, NumberCollections.findMax(integerLists, List::getFirst));
  }

  @Test
  void findMaxDoubleWithNull() {
    Double double0 = 1d;
    Double double2 = -1d;
    List<@Nullable Double> doubles = Arrays.asList(double0, null, double2, null);
    assertEquals(double0, NumberCollections.findMax(doubles, aDouble -> aDouble));
  }

  @Test
  void findMaxEmptyCollection() {
    assertNull(NumberCollections.findMax(List.<Integer>of(), anInteger -> anInteger));
  }
}
