package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Quantized16UnitIntervalDoublePrimitiveTest {
  @Test
  public void test() {
    // shortvariant [0,1)
    for (int i = 0; i < 10000; ++i) {
      double x = Math.random();
      double xQuantized =
          Quantized16UnitIntervalDoublePrimitive.toDouble(
              Quantized16UnitIntervalDoublePrimitive.toShort(x));
      assertEquals(x, xQuantized, 1E-4);
    }
  }

  @Test
  public void test1() {
    // shortvariant 1
    double x = 1d;
    double xQuantized =
        Quantized16UnitIntervalDoublePrimitive.toDouble(
            Quantized16UnitIntervalDoublePrimitive.toShort(x));
    assertEquals(x, xQuantized, 1E-4);
  }
}
