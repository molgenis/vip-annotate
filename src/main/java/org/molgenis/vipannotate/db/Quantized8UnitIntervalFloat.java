package org.molgenis.vipannotate.db;

/** Float greater than or equal to 0 and less than or equal to 1 stored as a byte */
public class Quantized8UnitIntervalFloat {
  private Quantized8UnitIntervalFloat() {}

  public static Float toFloat(byte quantizedFloatValue) {
    return quantizedFloatValue == 0 ? null : (Byte.toUnsignedInt(quantizedFloatValue) - 1) / 254f;
  }

  public static byte toByte(Float floatValue) {
    if (floatValue == null) {
      return 0;
    }
    if (floatValue < 0 || floatValue > 1) {
      throw new IllegalArgumentException();
    }
    return (byte) ((floatValue * 254) + 1);
  }
}
