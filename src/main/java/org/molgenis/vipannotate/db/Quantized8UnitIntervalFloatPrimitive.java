package org.molgenis.vipannotate.db;

/** Double greater than or equal to 0 and less than or equal to 1 stored as a byte */
public class Quantized8UnitIntervalFloatPrimitive {
  private Quantized8UnitIntervalFloatPrimitive() {}

  public static float toFloat(byte quantizedFloatValue) {
    return Byte.toUnsignedInt(quantizedFloatValue) / 255f;
  }

  public static byte toByte(Float floatValue) {
    if (floatValue < 0 || floatValue > 1) {
      throw new IllegalArgumentException();
    }
    return (byte) (floatValue * 255);
  }
}
