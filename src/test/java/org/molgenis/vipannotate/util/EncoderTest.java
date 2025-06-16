package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EncoderTest {

  @Test
  void encodeDecodeDoubleUnitIntervalFromShortPrimitive() {
    double maxEncoderError = 1d / (2 * ((1 << Short.SIZE) - 1));
    for (int i = 0; i < 1000; ++i) {
      double value = Math.random(); // [0, 1)
      short encodedValue = Encoder.encodeDoubleUnitIntervalPrimitiveAsShort(value);
      double decodedValue = Encoder.decodeDoubleUnitIntervalPrimitiveFromShort(encodedValue);
      assertEquals(value, decodedValue, maxEncoderError);
    }
  }

  @Test
  void encodeDecodeDoubleUnitIntervalFromShortPrimitiveOne() {
    double maxEncoderError = 1d / (2 * ((1 << Short.SIZE) - 1));
    double value = 1d;
    short encodedValue = Encoder.encodeDoubleUnitIntervalPrimitiveAsShort(value);
    double decodedValue = Encoder.decodeDoubleUnitIntervalPrimitiveFromShort(encodedValue);
    assertEquals(value, decodedValue, maxEncoderError);
  }

  @Test
  void encodeDecodeDoubleUnitIntervalFromShort() {
    double maxEncoderError = 1d / (2 * ((1 << Short.SIZE) - 2));
    for (int i = 0; i < 1000; ++i) {
      double value = Math.random(); // [0, 1)
      short encodedValue = Encoder.encodeDoubleUnitIntervalAsShort(value);
      Double decodedValue = Encoder.decodeDoubleUnitIntervalFromShort(encodedValue);
      assertEquals(value, decodedValue, maxEncoderError);
    }
  }

  @Test
  void encodeDecodeDoubleUnitIntervalFromShortOne() {
    double maxEncoderError = 1d / (2 * ((1 << Short.SIZE) - 2));
    double value = 1d;
    short encodedValue = Encoder.encodeDoubleUnitIntervalAsShort(value);
    Double decodedValue = Encoder.decodeDoubleUnitIntervalFromShort(encodedValue);
    assertEquals(value, decodedValue, maxEncoderError);
  }

  @Test
  void encodeDecodeDoubleUnitIntervalFromShortNull() {
    short encodedValue = Encoder.encodeDoubleUnitIntervalAsShort(null);
    Double decodedValue = Encoder.decodeDoubleUnitIntervalFromShort(encodedValue);
    assertNull(decodedValue);
  }
}
