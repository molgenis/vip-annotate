package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class QuantizerTest {
  private static Stream<Arguments> quantizeAndDequantizeProvider() {
    return Stream.of(
        Arguments.of(0d, 1d, 0, (1 << 8) - 1),
        Arguments.of(0d, 1d, 1, (1 << 8) - 1),
        Arguments.of(-5d, 5d, 1, (1 << 8) - 1),
        Arguments.of(0d, 1d, 0, (1 << 16) - 1),
        Arguments.of(0d, 1d, 1, (1 << 16) - 1));
  }

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

  @ParameterizedTest
  @MethodSource("quantizeAndDequantizeProvider")
  void quantizeAndDequantize(double x, double y, int u, int v) {
    double maxQuantizationError = (y - x) / (2 * (v - u));
    double totalQuantizationError = 0d;

    int nrIterations = 100000;
    Quantizer quantizer = new Quantizer();
    for (int i = 0; i < nrIterations; ++i) {
      double value = x + (y - x) * Math.random(); // number in [x,y)
      int quantizedValue =
          quantizer.quantize(value, new DoubleInterval(x, y), new IntInterval(u, v));
      double dequantizedValue =
          quantizer.dequantize(quantizedValue, new IntInterval(u, v), new DoubleInterval(x, y));
      totalQuantizationError += value - dequantizedValue;
      assertEquals(value, dequantizedValue, maxQuantizationError);
    }

    double meanQuantizationError = totalQuantizationError / nrIterations;
    assertEquals(0d, meanQuantizationError, 1E-4);
  }

  @ParameterizedTest
  @MethodSource("quantizeAndDequantizeProvider")
  void quantizeAndDequantizeRightClosed(double x, double yAndValue, int u, int v) {
    double maxQuantizationError = 1d / (2 * (v - u));

    Quantizer quantizer = new Quantizer();
    int quantizedValue =
        quantizer.quantize(yAndValue, new DoubleInterval(x, yAndValue), new IntInterval(u, v));
    double dequantizedValue =
        quantizer.dequantize(
            quantizedValue, new IntInterval(u, v), new DoubleInterval(x, yAndValue));

    assertEquals(yAndValue, dequantizedValue, maxQuantizationError);
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
