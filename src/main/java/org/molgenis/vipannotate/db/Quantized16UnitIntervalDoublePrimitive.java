package org.molgenis.vipannotate.db;

/** double greater than or equal to 0 and less than or equal to 1 stored as a short */
public class Quantized16UnitIntervalDoublePrimitive {
  private Quantized16UnitIntervalDoublePrimitive() {}

  public static double toDouble(short quantizedDoubleValue) {
    return Short.toUnsignedInt(quantizedDoubleValue) / 65535d;
  }

  public static short toShort(double doubleValue) {
    if (doubleValue < 0 || doubleValue > 1) {
      throw new IllegalArgumentException();
    }
    return (short) (doubleValue * 65535);
  }
}
