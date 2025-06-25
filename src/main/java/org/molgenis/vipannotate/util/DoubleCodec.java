package org.molgenis.vipannotate.util;

import org.jspecify.annotations.Nullable;

/** Double encoder and decoder. Encoding is lossy using a quantizer */
public class DoubleCodec {
  private static final int INTERVAL_BYTE_MAX = (1 << Byte.SIZE) - 1;
  private static final int INTERVAL_SHORT_MAX = (1 << Short.SIZE) - 1;

  private final Quantizer quantizer;

  public DoubleCodec() {
    this(new Quantizer());
  }

  DoubleCodec(Quantizer quantizer) {
    this.quantizer = quantizer;
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as byte. the maximum error after decoding an
   * encoded value is (y-x)/510.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded byte
   */
  public byte encodeDoubleAsByte(@Nullable Double value, double x, double y) {
    byte encodedValue;
    if (value != null) {
      int unsignedValue = quantizer.quantize(value, x, y, 1, INTERVAL_BYTE_MAX);
      encodedValue = (byte) unsignedValue;
    } else {
      encodedValue = 0;
    }
    return encodedValue;
  }

  /**
   * decodes a byte as double in [x,y]or <code>null</code>. the maximum error after decoding an
   * encoded value is (y-x)/510.
   *
   * @param value encoded value
   * @return double in [x,y] or <code>null</code>
   */
  public @Nullable Double decodeDoubleFromByte(byte value, double x, double y) {
    Double decodedValue;
    if (value != 0) {
      int unsignedValue = Byte.toUnsignedInt(value);
      decodedValue = quantizer.dequantize(unsignedValue, 1, INTERVAL_BYTE_MAX, x, y);
    } else {
      decodedValue = null;
    }
    return decodedValue;
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is 1/510.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded byte
   */
  public byte encodeDoubleUnitIntervalAsByte(@Nullable Double value) {
    return encodeDoubleAsByte(value, 0d, 1d);
  }

  /**
   * decodes a byte as double in [0,1] or <code>null</code>. the maximum error after decoding an
   * encoded value is 1/510.
   *
   * @param value encoded value
   * @return double in [0,1] or <code>null</code>
   */
  public @Nullable Double decodeDoubleUnitIntervalFromByte(byte value) {
    return decodeDoubleFromByte(value, 0d, 1d);
  }

  /**
   * encodes a double in [x,y] as byte. the maximum error after decoding an encoded value is
   * (y-x)/512.
   *
   * @param value double in [x,y]
   * @return encoded byte
   */
  public byte encodeDoublePrimitiveAsByte(double value, double x, double y) {
    int unsignedValue = quantizer.quantize(value, x, y, 0, INTERVAL_BYTE_MAX);
    return (byte) unsignedValue;
  }

  /**
   * decodes a byte as double in [x,y]. the maximum error after decoding an encoded value is
   * (y-x)/512.
   *
   * @param value encoded value
   * @return double in [x,y]
   */
  public double decodeDoublePrimitiveFromByte(byte value, double x, double y) {
    int unsignedInt = Byte.toUnsignedInt(value);
    return quantizer.dequantize(unsignedInt, 0, INTERVAL_BYTE_MAX, x, y);
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is (y-x)/131.070.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public short encodeDoubleAsShort(@Nullable Double value, double x, double y) {
    short encodedValue;
    if (value != null) {
      int unsignedValue = quantizer.quantize(value, x, y, 1, INTERVAL_SHORT_MAX);
      encodedValue = (short) unsignedValue;
    } else {
      encodedValue = 0;
    }
    return encodedValue;
  }

  /**
   * decodes a short as double in [x,y]or <code>null</code>. the maximum error after decoding an
   * encoded value is (y-x)/131.070.
   *
   * @param value encoded value
   * @return double in [x,y] or <code>null</code>
   */
  public @Nullable Double decodeDoubleFromShort(short value, double x, double y) {
    Double decodedValue;
    if (value != 0) {
      int unsignedValue = Short.toUnsignedInt(value);
      decodedValue = quantizer.dequantize(unsignedValue, 1, INTERVAL_SHORT_MAX, x, y);
    } else {
      decodedValue = null;
    }
    return decodedValue;
  }

  /**
   * encodes a double in [x,y] as short. the maximum error after decoding an encoded value is
   * (y-x)/131.072.
   *
   * @param value double in [x,y]
   * @return encoded short
   */
  public short encodeDoublePrimitiveAsShort(double value, double x, double y) {
    int unsignedValue = quantizer.quantize(value, x, y, 0, INTERVAL_SHORT_MAX);
    return (short) unsignedValue;
  }

  /**
   * decodes a short as double in [x,y]. the maximum error after decoding an encoded value is
   * (y-x)/131.072.
   *
   * @param value encoded value
   * @return double in [x,y]
   */
  public double decodeDoublePrimitiveFromShort(short value, double x, double y) {
    int unsignedInt = Short.toUnsignedInt(value);
    return quantizer.dequantize(unsignedInt, 0, INTERVAL_SHORT_MAX, x, y);
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is 1/131.070.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public short encodeDoubleUnitIntervalAsShort(@Nullable Double value) {
    return encodeDoubleAsShort(value, 0d, 1d);
  }

  /**
   * decodes a short as double in [0,1] or <code>null</code>. the maximum error after decoding an
   * encoded value is 1/131.070.
   *
   * @param value encoded value
   * @return double in [0,1]or <code>null</code>
   */
  public @Nullable Double decodeDoubleUnitIntervalFromShort(short value) {
    return decodeDoubleFromShort(value, 0d, 1d);
  }

  /**
   * encodes a double in [0,1] as short. the maximum error after decoding an encoded value is
   * 1/131.072.
   *
   * @param value double in [0,1]
   * @return encoded short
   */
  public short encodeDoubleUnitIntervalPrimitiveAsShort(double value) {
    return encodeDoublePrimitiveAsShort(value, 0d, 1d);
  }

  /**
   * decodes a short as double in [0,1]. the maximum error after decoding an encoded value is
   * 1/131.072.
   *
   * @param value encoded value
   * @return double in [0,1]
   */
  public double decodeDoubleUnitIntervalPrimitiveFromShort(short value) {
    return decodeDoublePrimitiveFromShort(value, 0d, 1d);
  }
}
