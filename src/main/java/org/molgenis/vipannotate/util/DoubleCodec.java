package org.molgenis.vipannotate.util;

import org.jspecify.annotations.Nullable;

/** Double encoder and decoder. Encoding is lossy using a quantizer */
public class DoubleCodec {
  private static final IntInterval QUANTIZATION_LEVELS_0_255 = new IntInterval(0, 255);
  private static final IntInterval QUANTIZATION_LEVELS_1_255 = new IntInterval(1, 255);
  private static final IntInterval QUANTIZATION_LEVELS_0_65535 = new IntInterval(0, 65535);
  private static final IntInterval QUANTIZATION_LEVELS_1_65535 = new IntInterval(1, 65535);

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
   * @param valueInterval value interval [x,y]
   * @return encoded byte
   */
  public byte encodeDoubleAsByte(@Nullable Double value, DoubleInterval valueInterval) {
    byte encodedValue;
    if (value != null) {
      int unsignedValue = quantizer.quantize(value, valueInterval, QUANTIZATION_LEVELS_1_255);
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
   * @param valueInterval value interval [x,y]
   * @return double in [x,y] or <code>null</code>
   */
  public @Nullable Double decodeDoubleFromByte(byte value, DoubleInterval valueInterval) {
    Double decodedValue;
    if (value != 0) {
      int unsignedValue = Byte.toUnsignedInt(value);
      decodedValue = quantizer.dequantize(unsignedValue, QUANTIZATION_LEVELS_1_255, valueInterval);
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
    return encodeDoubleAsByte(value, DoubleInterval.UNIT);
  }

  /**
   * decodes a byte as double in [0,1] or <code>null</code>. the maximum error after decoding an
   * encoded value is 1/510.
   *
   * @param value encoded value
   * @return double in [0,1] or <code>null</code>
   */
  public @Nullable Double decodeDoubleUnitIntervalFromByte(byte value) {
    return decodeDoubleFromByte(value, DoubleInterval.UNIT);
  }

  /**
   * encodes a double in [x,y] as byte. the maximum error after decoding an encoded value is
   * (y-x)/512.
   *
   * @param value double in [x,y]
   * @param valueInterval value interval [x,y]
   * @return encoded byte
   */
  public byte encodeDoublePrimitiveAsByte(double value, DoubleInterval valueInterval) {
    int unsignedValue = quantizer.quantize(value, valueInterval, QUANTIZATION_LEVELS_0_255);
    return (byte) unsignedValue;
  }

  /**
   * encodes a double in [0,1] as byte. the maximum error after decoding an encoded value is 1/512.
   *
   * @param value double in [0,1]
   * @return encoded byte
   */
  public byte encodeDoublePrimitiveUnitIntervalAsByte(double value) {
    return encodeDoublePrimitiveAsByte(value, DoubleInterval.UNIT);
  }

  /**
   * decodes a byte as double in [x,y]. the maximum error after decoding an encoded value is
   * (y-x)/512.
   *
   * @param value encoded value
   * @param valueInterval value interval [x,y]
   * @return double in [x,y]
   */
  public double decodeDoublePrimitiveFromByte(byte value, DoubleInterval valueInterval) {
    int unsignedInt = Byte.toUnsignedInt(value);
    return quantizer.dequantize(unsignedInt, QUANTIZATION_LEVELS_0_255, valueInterval);
  }

  /**
   * decodes a byte as double in [0,1]. the maximum error after decoding an encoded value is 1/512.
   *
   * @param value encoded value
   * @return double in [x,y]
   */
  public double decodeDoublePrimitiveUnitIntervalFromByte(byte value) {
    return decodeDoublePrimitiveFromByte(value, DoubleInterval.UNIT);
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is (y-x)/131.068.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public short encodeDoubleAsShort(@Nullable Double value, DoubleInterval valueInterval) {
    short encodedValue;
    if (value != null) {
      int unsignedValue = quantizer.quantize(value, valueInterval, QUANTIZATION_LEVELS_1_65535);
      encodedValue = (short) unsignedValue;
    } else {
      encodedValue = 0;
    }
    return encodedValue;
  }

  /**
   * decodes a short as double in [x,y] or <code>null</code>. the maximum error after decoding an
   * encoded value is (y-x)/131.068.
   *
   * @param value encoded value
   * @param valueInterval decoded value interval [x,y]
   * @return double in [x,y] or <code>null</code>
   */
  public @Nullable Double decodeDoubleFromShort(short value, DoubleInterval valueInterval) {
    Double decodedValue;
    if (value != 0) {
      int unsignedValue = Short.toUnsignedInt(value);
      decodedValue =
          quantizer.dequantize(unsignedValue, QUANTIZATION_LEVELS_1_65535, valueInterval);
    } else {
      decodedValue = null;
    }
    return decodedValue;
  }

  /**
   * encodes a double in [x,y] as short. the maximum error after decoding an encoded value is
   * (y-x)/131.070.
   *
   * @param value double in [x,y]
   * @param valueInterval value interval [x,y]
   * @return encoded short
   */
  public short encodeDoublePrimitiveAsShort(double value, DoubleInterval valueInterval) {
    int unsignedValue = quantizer.quantize(value, valueInterval, QUANTIZATION_LEVELS_0_65535);
    return (short) unsignedValue;
  }

  /**
   * decodes a short as double in [x,y]. the maximum error after decoding an encoded value is
   * (y-x)/131.070.
   *
   * @param value encoded value
   * @param valueInterval value interval [x,y]
   * @return double in [x,y]
   */
  public double decodeDoublePrimitiveFromShort(short value, DoubleInterval valueInterval) {
    int unsignedInt = Short.toUnsignedInt(value);
    return quantizer.dequantize(unsignedInt, QUANTIZATION_LEVELS_0_65535, valueInterval);
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is 1/131.068.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public short encodeDoubleUnitIntervalAsShort(@Nullable Double value) {
    return encodeDoubleAsShort(value, DoubleInterval.UNIT);
  }

  /**
   * decodes a short as double in [0,1] or <code>null</code>. the maximum error after decoding an
   * encoded value is 1/131.068.
   *
   * @param value encoded value
   * @return double in [0,1]or <code>null</code>
   */
  public @Nullable Double decodeDoubleUnitIntervalFromShort(short value) {
    return decodeDoubleFromShort(value, DoubleInterval.UNIT);
  }

  /**
   * encodes a double in [0,1] as short. the maximum error after decoding an encoded value is
   * 1/131.070.
   *
   * @param value double in [0,1]
   * @return encoded short
   */
  public short encodeDoubleUnitIntervalPrimitiveAsShort(double value) {
    return encodeDoublePrimitiveAsShort(value, DoubleInterval.UNIT);
  }

  /**
   * decodes a short as double in [0,1]. the maximum error after decoding an encoded value is
   * 1/131.070.
   *
   * @param value encoded value
   * @return double in [0,1]
   */
  public double decodeDoubleUnitIntervalPrimitiveFromShort(short value) {
    return decodeDoublePrimitiveFromShort(value, DoubleInterval.UNIT);
  }
}
