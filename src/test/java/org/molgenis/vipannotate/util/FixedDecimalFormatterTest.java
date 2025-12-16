package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import org.junit.jupiter.api.Test;

class FixedDecimalFormatterTest {
  @Test
  void appendFixed0() {
    Random random = new Random(42);
    DecimalFormat decimalFormat = createDecimalFormat("#");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      double value = (random.nextDouble() - 0.5) * 2_000_000; // doubles between -1e6 and +1e6
      stringBuilder.setLength(0);
      FixedDecimalFormatter.appendFixed0(stringBuilder, value);
      assertEquals(decimalFormat.format(value), stringBuilder.toString());
    }
  }

  @Test
  void appendFixed1() {
    Random random = new Random(42);
    DecimalFormat decimalFormat = createDecimalFormat("#.#");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      double value = (random.nextDouble() - 0.5) * 2_000_000; // doubles between -1e6 and +1e6
      stringBuilder.setLength(0);
      FixedDecimalFormatter.appendFixed1(stringBuilder, value);
      assertEquals(decimalFormat.format(value), stringBuilder.toString());
    }
  }

  @Test
  void appendFixed2() {
    Random random = new Random(42);
    DecimalFormat decimalFormat = createDecimalFormat("#.##");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      double value = (random.nextDouble() - 0.5) * 2_000_000; // doubles between -1e6 and +1e6
      stringBuilder.setLength(0);
      FixedDecimalFormatter.appendFixed2(stringBuilder, value);
      assertEquals(decimalFormat.format(value), stringBuilder.toString());
    }
  }

  @Test
  void appendFixed3() {
    Random random = new Random(42);
    DecimalFormat decimalFormat = createDecimalFormat("#.###");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      double value = (random.nextDouble() - 0.5) * 2_000_000; // doubles between -1e6 and +1e6
      stringBuilder.setLength(0);
      FixedDecimalFormatter.appendFixed3(stringBuilder, value);
      assertEquals(decimalFormat.format(value), stringBuilder.toString());
    }
  }

  @Test
  void appendFixed4() {
    Random random = new Random(42);
    DecimalFormat decimalFormat = createDecimalFormat("#.####");
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 100000; i++) {
      double value = (random.nextDouble() - 0.5) * 2_000_000; // doubles between -1e6 and +1e6
      stringBuilder.setLength(0);
      FixedDecimalFormatter.appendFixed4(stringBuilder, value);
      assertEquals(decimalFormat.format(value), stringBuilder.toString());
    }
  }

  @SuppressWarnings("DataFlowIssue")
  private DecimalFormat createDecimalFormat(String pattern) {
    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    decimalFormat.setStrict(true);
    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
    decimalFormat.applyPattern(pattern);
    return decimalFormat;
  }
}
