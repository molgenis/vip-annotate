package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoubleCodecTest {
  private DoubleCodec doubleCodec;
  private Quantizer quantizer;

  @BeforeEach
  void setUp() {
    quantizer = mock(Quantizer.class);
    doubleCodec = new DoubleCodec(quantizer);
  }

  @Test
  void encodeDoubleAsByteInSignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 255));
    assertEquals(100, doubleCodec.encodeDoubleAsByte(value, valueInterval));
  }

  @Test
  void encodeDoubleAsByteInUnsignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(200).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 255));
    assertEquals(-56, doubleCodec.encodeDoubleAsByte(value, valueInterval));
  }

  @Test
  void encodeDoubleAsByteNull() {
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    assertEquals(0, doubleCodec.encodeDoubleAsByte(null, valueInterval));
  }

  @Test
  void decodeDoubleFromByteInSignedRange() {
    byte value = 100;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(100, new IntInterval(1, 255), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleFromByte(value, valueInterval);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromByteInUnsignedRange() {
    byte value = -56;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(200, new IntInterval(1, 255), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleFromByte(value, valueInterval);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromByteNull() {
    byte value = 0;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    assertNull(doubleCodec.decodeDoubleFromByte(value, valueInterval));
  }

  @Test
  void encodeDoubleUnitIntervalAsByte() {
    double value = 0.23d;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 255));
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalAsByte(value));
  }

  @Test
  void decodeDoubleUnitIntervalFromByte() {
    byte value = 100;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(0.23d).when(quantizer).dequantize(100, new IntInterval(1, 255), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleUnitIntervalFromByte(value);
    assertNotNull(decodedValue);
    assertEquals(0.23d, decodedValue, 1E-6);
  }

  @Test
  void encodeDoublePrimitiveAsByteInSignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 255));
    assertEquals(100, doubleCodec.encodeDoublePrimitiveAsByte(value, valueInterval));
  }

  @Test
  void encodeDoublePrimitiveAsByteInUnsignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(200).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 255));
    assertEquals(-56, doubleCodec.encodeDoublePrimitiveAsByte(value, valueInterval));
  }

  @Test
  void encodeDoublePrimitiveUnitIntervalAsByte() {
    double value = 0.23d;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 255));
    assertEquals(100, doubleCodec.encodeDoublePrimitiveUnitIntervalAsByte(value));
  }

  @Test
  void decodeDoublePrimitiveFromByteInSignedRange() {
    byte value = 100;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(100, new IntInterval(0, 255), valueInterval);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromByte(value, valueInterval), 1E-6);
  }

  @Test
  void decodeDoublePrimitiveFromByteInUnsignedRange() {
    byte value = -56;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(200, new IntInterval(0, 255), valueInterval);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromByte(value, valueInterval), 1E-6);
  }

  @Test
  void decodeDoublePrimitiveUnitIntervalFromByte() {
    byte value = 100;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(0.23d).when(quantizer).dequantize(100, new IntInterval(0, 255), valueInterval);
    double decodedValue = doubleCodec.decodeDoublePrimitiveUnitIntervalFromByte(value);
    assertEquals(0.23d, decodedValue, 1E-6);
  }

  @Test
  void encodeDoubleAsShortInSignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 65535));
    assertEquals(100, doubleCodec.encodeDoubleAsShort(value, valueInterval));
  }

  @Test
  void encodeDoubleAsShortInUnsignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(50000).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 65535));
    assertEquals(-15536, doubleCodec.encodeDoubleAsShort(value, valueInterval));
  }

  @Test
  void encodeDoubleAsShortNull() {
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    assertEquals(0, doubleCodec.encodeDoubleAsShort(null, valueInterval));
  }

  @Test
  void decodeDoubleFromShortInSignedRange() {
    short value = 100;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(100, new IntInterval(1, 65535), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleFromShort(value, valueInterval);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromShortInUnsignedRange() {
    short value = -15536;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(50000, new IntInterval(1, 65535), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleFromShort(value, valueInterval);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromShortNull() {
    short value = 0;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    assertNull(doubleCodec.decodeDoubleFromShort(value, valueInterval));
  }

  @Test
  void encodeDoubleUnitIntervalAsShort() {
    double value = 0.23d;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(1, 65535));
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalAsShort(value));
  }

  @Test
  void decodeDoubleUnitIntervalFromShort() {
    short value = 100;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(0.23d).when(quantizer).dequantize(100, new IntInterval(1, 65535), valueInterval);
    Double decodedValue = doubleCodec.decodeDoubleUnitIntervalFromShort(value);
    assertNotNull(decodedValue);
    assertEquals(0.23d, decodedValue, 1E-6);
  }

  @Test
  void encodeDoublePrimitiveAsShortInSignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 65535));
    assertEquals(100, doubleCodec.encodeDoublePrimitiveAsShort(value, valueInterval));
  }

  @Test
  void encodeDoublePrimitiveAsShortInUnsignedRange() {
    double value = 1.23d;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(50000).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 65535));
    assertEquals(-15536, doubleCodec.encodeDoublePrimitiveAsShort(value, valueInterval));
  }

  @Test
  void decodeDoublePrimitiveFromShortInSignedRange() {
    short value = 100;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(100, new IntInterval(0, 65535), valueInterval);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromShort(value, valueInterval), 1E-6);
  }

  @Test
  void decodeDoublePrimitiveFromShortInUnsignedRange() {
    short value = -15536;
    DoubleInterval valueInterval = new DoubleInterval(-2d, 2d);
    doReturn(1.23d).when(quantizer).dequantize(50000, new IntInterval(0, 65535), valueInterval);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromShort(value, valueInterval), 1E-6);
  }

  @Test
  void encodeDoublePrimitiveUnitIntervalAsShort() {
    double value = 0.23d;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(100).when(quantizer).quantize(value, valueInterval, new IntInterval(0, 65535));
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalPrimitiveAsShort(value));
  }

  @Test
  void decodeDoublePrimitiveUnitIntervalFromShort() {
    short value = 100;
    DoubleInterval valueInterval = new DoubleInterval(0d, 1d);
    doReturn(0.23d).when(quantizer).dequantize(100, new IntInterval(0, 65535), valueInterval);
    assertEquals(0.23d, doubleCodec.decodeDoubleUnitIntervalPrimitiveFromShort(value), 1E-6);
  }
}
