package org.molgenis.vipannotate.util;

public class Encoder {
  private static final int INTERVAL_SHORT_MAX = (1 << Short.SIZE) - 1;

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is (y-x)/508.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public static short encodeDoubleAsShort(Double value, double x, double y) {
    short encodedValue;
    if (value != null) {
      int unsignedValue = Quantizer.quantize(value, x, y, 1, INTERVAL_SHORT_MAX);
      encodedValue = (short) unsignedValue;
    } else {
      encodedValue = 0;
    }
    return encodedValue;
  }

  /**
   * decodes a short as double in [x,y]or <code>null</code>. the maximum error after decoding an
   * encoded value is (y-x)/508.
   *
   * @param value encoded value
   * @return double in [x,y] or <code>null</code>
   */
  public static Double decodeDoubleFromShort(short value, double x, double y) {
    Double decodedValue;
    if (value != 0) {
      int unsignedValue = Short.toUnsignedInt(value);
      decodedValue = Quantizer.dequantize(unsignedValue, 1, INTERVAL_SHORT_MAX, x, y);
    } else {
      decodedValue = null;
    }
    return decodedValue;
  }

  /**
   * encodes a double in [x,y] as short. the maximum error after decoding an encoded value is
   * (y-x)/510.
   *
   * @param value double in [x,y]
   * @return encoded short
   */
  public static short encodeDoublePrimitiveAsShort(double value, double x, double y) {
    int unsignedValue = Quantizer.quantize(value, x, y, 0, INTERVAL_SHORT_MAX);
    return (short) unsignedValue;
  }

  /**
   * decodes a short as double in [x,y]. the maximum error after decoding an encoded value is
   * (y-x)/510.
   *
   * @param value encoded value
   * @return double in [x,y]
   */
  public static double decodeDoublePrimitiveFromShort(short value, double x, double y) {
    int unsignedInt = Short.toUnsignedInt(value);
    return Quantizer.dequantize(unsignedInt, 0, INTERVAL_SHORT_MAX, x, y);
  }

  /**
   * encodes a double in [x,y] or <code>null</code> as short. the maximum error after decoding an
   * encoded value is 1/508=0.0019685.
   *
   * @param value double in [x,y] or <code>null</code>
   * @return encoded short
   */
  public static short encodeUnitIntervalDoubleAsShort(Double value) {
    return encodeDoubleAsShort(value, 0d, 1d);
  }

  /**
   * decodes a short as double in [0,1] or <code>null</code>. the maximum error after decoding an
   * encoded value is 1/508=0.0019685.
   *
   * @param value encoded value
   * @return double in [0,1]or <code>null</code>
   */
  public static Double decodeUnitIntervalDoubleFromShort(short value) {
    return decodeDoubleFromShort(value, 0d, 1d);
  }

  /**
   * encodes a double in [0,1] as short. the maximum error after decoding an encoded value is
   * 1/510=0.00196.
   *
   * @param value double in [0,1]
   * @return encoded short
   */
  public static short encodeUnitIntervalDoublePrimitiveAsShort(double value) {
    return encodeDoublePrimitiveAsShort(value, 0d, 1d);
  }

  /**
   * decodes a short as double in [0,1]. the maximum error after decoding an encoded value is
   * 1/510=0.00196.
   *
   * @param value encoded value
   * @return double in [0,1]
   */
  public static double decodeUnitIntervalDoublePrimitiveFromShort(short value) {
    return decodeDoublePrimitiveFromShort(value, 0d, 1d);
  }
}
