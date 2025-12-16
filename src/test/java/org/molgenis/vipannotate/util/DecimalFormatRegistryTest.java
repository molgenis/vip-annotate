package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.junit.jupiter.api.Test;

class DecimalFormatRegistryTest {
  @Test
  void get() {
    DecimalFormat decimalFormat = DecimalFormatRegistry.INSTANCE.get("#.##");
    assertAll(
        () -> assertEquals(0, decimalFormat.getMinimumFractionDigits()),
        () -> assertEquals(2, decimalFormat.getMaximumFractionDigits()),
        () -> assertEquals(RoundingMode.HALF_UP, decimalFormat.getRoundingMode()));
  }

  @Test
  void getSame() {
    DecimalFormat decimalFormat = DecimalFormatRegistry.INSTANCE.get("#.##");
    assertAll(
        () -> assertSame(decimalFormat, DecimalFormatRegistry.INSTANCE.get("#.##")),
        () -> assertNotSame(decimalFormat, DecimalFormatRegistry.INSTANCE.get("#.###")));
  }
}
