package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IntIntervalTest {
  @Test
  void intervalValid() {
    IntInterval interval = new IntInterval(-1, 1);
    assertAll(() -> assertEquals(-1d, interval.min()), () -> assertEquals(1d, interval.max()));
  }

  @Test
  void intervalValidMinIsMax() {
    IntInterval interval = new IntInterval(1, 1);
    assertAll(() -> assertEquals(1, interval.min()), () -> assertEquals(1, interval.max()));
  }

  @Test
  void intervalInvalid() {
    assertThrows(IllegalArgumentException.class, () -> new IntInterval(1, -1));
  }
}
