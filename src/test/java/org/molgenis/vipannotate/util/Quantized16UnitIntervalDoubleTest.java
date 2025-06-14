package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class Quantized16UnitIntervalDoubleTest {
  @Test
  public void test() {
    // [0,1)
    for (int i = 0; i < 10000; ++i) {
      double x = Math.random();
      double xQuantized =
          Quantized16UnitIntervalDouble.toDouble(Quantized16UnitIntervalDouble.toShort(x));
      assertEquals(x, xQuantized, 1E-4);
    }
  }

  @Test
  public void test1() {
    double x = 1d;
    double xQuantized =
        Quantized16UnitIntervalDouble.toDouble(Quantized16UnitIntervalDouble.toShort(x));
    assertEquals(x, xQuantized, 1E-4);
  }

  @Test
  public void testNull() {
    Double x = null;
    Double xQuantized =
        Quantized16UnitIntervalDouble.toDouble(Quantized16UnitIntervalDouble.toShort(x));
    assertNull(xQuantized);
  }
}
