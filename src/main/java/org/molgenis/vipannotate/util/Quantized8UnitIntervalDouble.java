package org.molgenis.vipannotate.util;

/** Double greater than or equal to 0 and less than or equal to 1 stored as a byte */
public class Quantized8UnitIntervalDouble {
  private Quantized8UnitIntervalDouble() {}

  public static Double toDouble(byte quantizedDoubleValue) {
    return quantizedDoubleValue == 0 ? null : (Byte.toUnsignedInt(quantizedDoubleValue) - 1) / 254d;
  }

  public static byte toByte(Double doubleValue) {
    if (doubleValue == null) {
      return 0;
    }
    if (doubleValue < 0 || doubleValue > 1) {
      throw new IllegalArgumentException();
    }
    return (byte) ((doubleValue * 254) + 1);
  }
}
