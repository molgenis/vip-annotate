package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class Quantized8UnitIntervalFloatTest {
  @Test
  public void test() {
    // [0,1)
    for (int i = 0; i < 10000; ++i) {
      float x = (float) Math.random();
      Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
      assertNotNull(xQuantized);
      assertEquals(x, xQuantized, 1E-2);
    }
  }

  @Test
  public void test1() {
    float x = 1f;
    Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
    assertNotNull(xQuantized);
    assertEquals(x, xQuantized, 1E-2);
  }

  @Test
  public void testNull() {
    Float x = null;
    Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
    assertNull(xQuantized);
  }
}
