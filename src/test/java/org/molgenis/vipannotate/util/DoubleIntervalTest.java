package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DoubleIntervalTest {
  @Test
  void intervalValid() {
    DoubleInterval interval = new DoubleInterval(-1d, 1d);
    assertAll(
        () -> assertEquals(-1d, interval.min(), 1E-6),
        () -> assertEquals(1d, interval.max(), 1E-6));
  }

  @Test
  void intervalValidMinIsMax() {
    DoubleInterval interval = new DoubleInterval(1d, 1d);
    assertAll(
        () -> assertEquals(1d, interval.min(), 1E-6), () -> assertEquals(1d, interval.max(), 1E-6));
  }

  @Test
  void intervalInvalid() {
    assertThrows(IllegalArgumentException.class, () -> new DoubleInterval(1d, -1d));
  }

  @Test
  void contains() {
    DoubleInterval interval = new DoubleInterval(-1d, 1d);
    assertAll(
        () -> assertFalse(interval.contains(-2d)),
        () -> assertTrue(interval.contains(-1d)),
        () -> assertTrue(interval.contains(0d)),
        () -> assertTrue(interval.contains(1d)),
        () -> assertFalse(interval.contains(2d)));
  }
}
