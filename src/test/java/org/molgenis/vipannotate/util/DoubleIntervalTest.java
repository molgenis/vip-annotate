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
}
