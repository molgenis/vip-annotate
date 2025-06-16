package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EncoderTest {

  @Test
  void encodeDecodeUnitIntervalDoubleFromShortPrimitive() {
    double maxEncoderError = 1d / (2 * ((1 << Short.BYTES) - 1));
    for (int i = 0; i < 1000; ++i) {
      double value = Math.random(); // [0, 1)
      short encodedValue = Encoder.encodeUnitIntervalDoublePrimitiveAsShort(value);
      double decodedValue = Encoder.decodeUnitIntervalDoublePrimitiveFromShort(encodedValue);
      assertEquals(value, decodedValue, maxEncoderError);
    }
  }

  @Test
  void encodeDecodeUnitIntervalDoubleFromShortPrimitiveOne() {
    double maxEncoderError = 1d / (2 * ((1 << Short.BYTES) - 1));
    double value = 1d;
    short encodedValue = Encoder.encodeUnitIntervalDoublePrimitiveAsShort(value);
    double decodedValue = Encoder.decodeUnitIntervalDoublePrimitiveFromShort(encodedValue);
    assertEquals(value, decodedValue, maxEncoderError);
  }

  @Test
  void encodeDecodeUnitIntervalDoubleFromShort() {
    double maxEncoderError = 1d / (2 * ((1 << Short.BYTES) - 2));
    for (int i = 0; i < 1000; ++i) {
      double value = Math.random(); // [0, 1)
      short encodedValue = Encoder.encodeUnitIntervalDoubleAsShort(value);
      Double decodedValue = Encoder.decodeUnitIntervalDoubleFromShort(encodedValue);
      assertEquals(value, decodedValue, maxEncoderError);
    }
  }

  @Test
  void encodeDecodeUnitIntervalDoubleFromShortOne() {
    double maxEncoderError = 1d / (2 * ((1 << Short.BYTES) - 2));
    double value = 1d;
    short encodedValue = Encoder.encodeUnitIntervalDoubleAsShort(value);
    Double decodedValue = Encoder.decodeUnitIntervalDoubleFromShort(encodedValue);
    assertEquals(value, decodedValue, maxEncoderError);
  }

  @Test
  void encodeDecodeUnitIntervalDoubleFromShortNull() {
    short encodedValue = Encoder.encodeUnitIntervalDoubleAsShort(null);
    Double decodedValue = Encoder.decodeUnitIntervalDoubleFromShort(encodedValue);
    assertNull(decodedValue);
  }
}
