package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class QuantizerTest {
  @Test
  void quantizeInvalidSourceInterval() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().quantize(1d, new DoubleInterval(1d, 0d), new IntInterval(0, 1)));
  }

  @Test
  void quantizeInvalidTargetInterval() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().quantize(1d, new DoubleInterval(0d, 1d), new IntInterval(1, 0)));
  }

  @Test
  void quantizeInvalidValueOutsideOfRangeLeft() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().quantize(-1d, new DoubleInterval(0d, 1d), new IntInterval(0, 1)));
  }

  @Test
  void quantizeInvalidValueOutsideOfRangeRight() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().quantize(2d, new DoubleInterval(0d, 1d), new IntInterval(0, 1)));
  }

  @Test
  void dequantizeInvalidSourceInterval() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().dequantize(1, new IntInterval(0, 1), new DoubleInterval(1d, 0d)));
  }

  @Test
  void dequantizeInvalidTargetInterval() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().dequantize(1, new IntInterval(1, 0), new DoubleInterval(0d, 1d)));
  }

  @Test
  void dequantizeInvalidValueOutsideOfRangeLeft() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().dequantize(-1, new IntInterval(0, 1), new DoubleInterval(0d, 1d)));
  }

  @Test
  void dequantizeInvalidValueOutsideOfRangeRight() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Quantizer().dequantize(2, new IntInterval(0, 1), new DoubleInterval(0d, 1d)));
  }
}
