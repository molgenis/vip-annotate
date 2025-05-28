package org.molgenis.vcf.annotate.db;

public class Quantizer {

  /**
   * Quantizes a Double in the range [0.0, 1.0] into an int in the range [1, 255], reserving 0 to
   * represent null.
   *
   * <ul>
   *   <li>Step size: 1 / 254 ≈ 0.003937
   *   <li>Maximum absolute error: 1 / (2 × 254) ≈ 0.0019685
   * </ul>
   *
   * @param value The Double value (may be null).
   * @return An int in [0, 255]; 0 indicates null, and 1–255 represent values in [0.0, 1.0].
   * @throws IllegalArgumentException if value is not in [0.0, 1.0]
   */
  public static int quantizeToByte(Double value) {
    if (value == null) {
      return 0;
    }
    if (value < 0.0 || value > 1.0 || Double.isNaN(value)) {
      throw new IllegalArgumentException("Value must be in [0.0, 1.0]");
    }
    return 1 + (int) Math.round(value * 254);
  }

  /**
   * Dequantizes an int in [0, 255] back to a Double in [0.0, 1.0], returning null if input is 0.
   *
   * @param value The quantized int in [0, 255].
   * @return A Double in [0.0, 1.0] if value in the range [1, 255], or null if value == 0.
   * @throws IllegalArgumentException if value is not in [0, 255]
   */
  public static Double dequantizeFromByte(int value) {
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException("Quantized value must be in [0, 255]");
    }
    if (value == 0) {
      return null;
    }
    return (value - 1) / 254.0;
  }

  public static void main(String[] args) {
    int i = Quantizer.quantizeToByte(0.105877);
    Double v = Quantizer.dequantizeFromByte(i);
    System.out.println(i);
    System.out.println(v);
  }
}
