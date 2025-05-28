package org.molgenis.vcf.annotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.annotate.db.Quantized8UnitIntervalFloat;

public class Quantized8UnitIntervalFloatTest {
  @Test
  public void test() {
    // test [0,1)
    for (int i = 0; i < 10000; ++i) {
      float x = (float) Math.random();
      Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
      assertNotNull(xQuantized);
      assertEquals(x, xQuantized, 1E-2);
    }
  }

  @Test
  public void test1() {
    // test 1
    float x = 1f;
    Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
    assertNotNull(xQuantized);
    assertEquals(x, xQuantized, 1E-2);
  }

  @Test
  public void testNull() {
    // test null
    Float x = null;
    Float xQuantized = Quantized8UnitIntervalFloat.toFloat(Quantized8UnitIntervalFloat.toByte(x));
    assertNull(xQuantized);
  }
}
