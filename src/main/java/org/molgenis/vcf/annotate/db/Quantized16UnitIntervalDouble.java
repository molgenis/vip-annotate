package org.molgenis.vcf.annotate.db;

/** Double greater than or equal to 0 and less than or equal to 1 or null stored as a short */
public class Quantized16UnitIntervalDouble {
  private Quantized16UnitIntervalDouble() {}

  public static Double toDouble(short quantizedDoubleValue) {
    return quantizedDoubleValue == 0 ? null : Short.toUnsignedInt(quantizedDoubleValue) / 65534d;
  }

  public static short toShort(Double doubleValue) {
    if (doubleValue == null) {
      return 0;
    }
    if (doubleValue < 0 || doubleValue > 1) {
      throw new IllegalArgumentException();
    }
    return (short) ((doubleValue * 65534) + 1);
  }
}
