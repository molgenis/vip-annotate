package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class QuantizerTest {
  private static Stream<Arguments> quantizeProvider() {
    return Stream.of(
        Arguments.of(0.0, 1.0, 0, 4, 0.0, 0L),
        Arguments.of(0.0, 1.0, 0, 4, 0.125, 1L),
        Arguments.of(0.0, 1.0, 0, 4, 0.1, 0L),
        Arguments.of(0.0, 1.0, 0, 4, 0.25, 1L),
        Arguments.of(0.0, 1.0, 0, 4, 0.375, 2L),
        Arguments.of(0.0, 1.0, 0, 4, 0.5, 2L),
        Arguments.of(0.0, 1.0, 0, 4, 0.875, 4L),
        Arguments.of(0.0, 1.0, 0, 4, 1.0, 4L),
        Arguments.of(-1.5, 1.5, 0, 6, -1.5, 0L),
        Arguments.of(-1.5, 1.5, 0, 6, -0.75, 2L),
        Arguments.of(-1.5, 1.5, 0, 6, 0.0, 3L),
        Arguments.of(-1.5, 1.5, 0, 6, 0.75, 5L),
        Arguments.of(-1.5, 1.5, 0, 6, 1.5, 6L),
        Arguments.of(10.25, 10.75, 5, 7, 10.25, 5L),
        Arguments.of(10.25, 10.75, 5, 7, 10.5, 6L),
        Arguments.of(10.25, 10.75, 5, 7, 10.75, 7L));
  }

  @ParameterizedTest
  @MethodSource("quantizeProvider")
  void quantize(double x, double y, int u, int v, double value, long expected) {
    Quantizer quantizer = new Quantizer(new DoubleInterval(x, y), new IntInterval(u, v));
    assertEquals(expected, quantizer.quantize(value));
  }

  @Test
  void quantizeOutOfRange() {
    Quantizer quantizer = new Quantizer(new DoubleInterval(0.0, 1.0), new IntInterval(0, 128));
    assertThrows(IllegalArgumentException.class, () -> quantizer.quantize(-1.0));
  }

  private static Stream<Arguments> dequantizeProvider() {
    return Stream.of(
        Arguments.of(0.0, 1.0, 0, 4, 0L, 0.0),
        Arguments.of(0.0, 1.0, 0, 4, 1L, 0.25),
        Arguments.of(0.0, 1.0, 0, 4, 2L, 0.5),
        Arguments.of(0.0, 1.0, 0, 4, 3L, 0.75),
        Arguments.of(0.0, 1.0, 0, 4, 4L, 1.0),
        Arguments.of(-1.5, 1.5, 0, 6, 0L, -1.5),
        Arguments.of(-1.5, 1.5, 0, 6, 1L, -1.0),
        Arguments.of(-1.5, 1.5, 0, 6, 2L, -0.5),
        Arguments.of(-1.5, 1.5, 0, 6, 3L, 0.0),
        Arguments.of(-1.5, 1.5, 0, 6, 4L, 0.5),
        Arguments.of(-1.5, 1.5, 0, 6, 5L, 1.0),
        Arguments.of(-1.5, 1.5, 0, 6, 6L, 1.5),
        Arguments.of(10.25, 10.75, 5, 7, 5L, 10.25),
        Arguments.of(10.25, 10.75, 5, 7, 6L, 10.5),
        Arguments.of(10.25, 10.75, 5, 7, 7L, 10.75));
  }

  @Test
  void dequantizeOutOfRange() {
    Quantizer quantizer = new Quantizer(new DoubleInterval(0.0, 1.0), new IntInterval(0, 128));
    assertThrows(IllegalArgumentException.class, () -> quantizer.dequantize(129));
  }

  @ParameterizedTest
  @MethodSource("dequantizeProvider")
  void dequantize(double x, double y, int u, int v, long value, double expected) {
    Quantizer quantizer = new Quantizer(new DoubleInterval(x, y), new IntInterval(u, v));
    double maxError = (y - x) / (double) (u - v);
    assertEquals(expected, quantizer.dequantize(value), Math.abs(maxError));
  }
}
