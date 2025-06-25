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
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, x, y, 1, 255);
    assertEquals(100, doubleCodec.encodeDoubleAsByte(value, x, y));
  }

  @Test
  void encodeDoubleAsByteInUnsignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(200).when(quantizer).quantize(value, x, y, 1, 255);
    assertEquals(-56, doubleCodec.encodeDoubleAsByte(value, x, y));
  }

  @Test
  void encodeDoubleAsByteNull() {
    assertEquals(0, doubleCodec.encodeDoubleAsByte(null, -2d, 2d));
  }

  @Test
  void decodeDoubleFromByteInSignedRange() {
    byte value = 100;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 1, 255, x, y);
    Double decodedValue = doubleCodec.decodeDoubleFromByte(value, x, y);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromByteInUnsignedRange() {
    byte value = -56;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(200, 1, 255, x, y);
    Double decodedValue = doubleCodec.decodeDoubleFromByte(value, x, y);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromByteNull() {
    byte value = 0;
    assertNull(doubleCodec.decodeDoubleFromByte(value, -2d, 2d));
  }

  @Test
  void encodeDoubleUnitIntervalAsByte() {
    double value = 1.23d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, 0d, 1d, 1, 255);
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalAsByte(value));
  }

  @Test
  void decodeDoubleUnitIntervalFromByte() {
    byte value = 100;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 1, 255, 0d, 1d);
    Double decodedValue = doubleCodec.decodeDoubleUnitIntervalFromByte(value);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void encodeDoublePrimitiveAsByteInSignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, x, y, 0, 255);
    assertEquals(100, doubleCodec.encodeDoublePrimitiveAsByte(value, x, y));
  }

  @Test
  void encodeDoublePrimitiveAsByteInUnsignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(200).when(quantizer).quantize(value, x, y, 0, 255);
    assertEquals(-56, doubleCodec.encodeDoublePrimitiveAsByte(value, x, y));
  }

  @Test
  void decodeDoublePrimitiveFromByteInSignedRange() {
    byte value = 100;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 0, 255, x, y);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromByte(value, x, y), 1E-6);
  }

  @Test
  void decodeDoublePrimitiveFromByteInUnsignedRange() {
    byte value = -56;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(200, 0, 255, x, y);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromByte(value, x, y), 1E-6);
  }

  @Test
  void encodeDoubleAsShortInSignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, x, y, 1, 65535);
    assertEquals(100, doubleCodec.encodeDoubleAsShort(value, x, y));
  }

  @Test
  void encodeDoubleAsShortInUnsignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(50000).when(quantizer).quantize(value, x, y, 1, 65535);
    assertEquals(-15536, doubleCodec.encodeDoubleAsShort(value, x, y));
  }

  @Test
  void encodeDoubleAsShortNull() {
    assertEquals(0, doubleCodec.encodeDoubleAsShort(null, -2d, 2d));
  }

  @Test
  void decodeDoubleFromShortInSignedRange() {
    short value = 100;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 1, 65535, x, y);
    Double decodedValue = doubleCodec.decodeDoubleFromShort(value, x, y);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromShortInUnsignedRange() {
    short value = -15536;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(50000, 1, 65535, x, y);
    Double decodedValue = doubleCodec.decodeDoubleFromShort(value, x, y);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void decodeDoubleFromShortNull() {
    short value = 0;
    assertNull(doubleCodec.decodeDoubleFromShort(value, -2d, 2d));
  }

  @Test
  void encodeDoubleUnitIntervalAsShort() {
    double value = 1.23d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, 0d, 1d, 1, 65535);
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalAsShort(value));
  }

  @Test
  void decodeDoubleUnitIntervalFromShort() {
    short value = 100;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 1, 65535, 0d, 1d);
    Double decodedValue = doubleCodec.decodeDoubleUnitIntervalFromShort(value);
    assertNotNull(decodedValue);
    assertEquals(1.23d, decodedValue, 1E-6);
  }

  @Test
  void encodeDoublePrimitiveAsShortInSignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, x, y, 0, 65535);
    assertEquals(100, doubleCodec.encodeDoublePrimitiveAsShort(value, x, y));
  }

  @Test
  void encodeDoublePrimitiveAsShortInUnsignedRange() {
    double value = 1.23d;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(50000).when(quantizer).quantize(value, x, y, 0, 65535);
    assertEquals(-15536, doubleCodec.encodeDoublePrimitiveAsShort(value, x, y));
  }

  @Test
  void decodeDoublePrimitiveFromShortInSignedRange() {
    short value = 100;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 0, 65535, x, y);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromShort(value, x, y), 1E-6);
  }

  @Test
  void decodeDoublePrimitiveFromShortInUnsignedRange() {
    short value = -15536;
    double x = -2d;
    double y = 2d;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(50000, 0, 65535, x, y);
    assertEquals(1.23d, doubleCodec.decodeDoublePrimitiveFromShort(value, x, y), 1E-6);
  }

  @Test
  void encodeDoublePrimitiveUnitIntervalAsShort() {
    double value = 1.23d;
    //noinspection DataFlowIssue
    doReturn(100).when(quantizer).quantize(value, 0d, 1d, 0, 65535);
    assertEquals(100, doubleCodec.encodeDoubleUnitIntervalPrimitiveAsShort(value));
  }

  @Test
  void decodeDoublePrimitiveUnitIntervalFromShort() {
    short value = 100;
    //noinspection DataFlowIssue
    doReturn(1.23d).when(quantizer).dequantize(100, 0, 65535, 0d, 1d);
    assertEquals(1.23d, doubleCodec.decodeDoubleUnitIntervalPrimitiveFromShort(value), 1E-6);
  }
}
