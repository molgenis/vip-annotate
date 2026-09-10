package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IntIntervalTest {
  @Test
  void intervalValid() {
    IntInterval interval = new IntInterval(-1L, 1L);
    assertAll(() -> assertEquals(-1L, interval.min()), () -> assertEquals(1L, interval.max()));
  }

  @Test
  void intervalValidMinIsMax() {
    IntInterval interval = new IntInterval(1L, 1L);
    assertAll(() -> assertEquals(1L, interval.min()), () -> assertEquals(1L, interval.max()));
  }

  @Test
  void intervalInvalid() {
    assertThrows(IllegalArgumentException.class, () -> new IntInterval(1, -1));
  }

  @Test
  void contains() {
    IntInterval interval = new IntInterval(-1L, 1L);
    assertAll(
        () -> assertFalse(interval.contains(-2)),
        () -> assertTrue(interval.contains(-1)),
        () -> assertTrue(interval.contains(0)),
        () -> assertTrue(interval.contains(1)),
        () -> assertFalse(interval.contains(2)));
  }
}
