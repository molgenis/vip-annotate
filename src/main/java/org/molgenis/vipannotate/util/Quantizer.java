package org.molgenis.vipannotate.util;

public class Quantizer {
  /**
   * quantize a double value in the interval [x,y] into an integer in the interval [u, v]. the
   * maximum error of dequantize(quantize(value)) is 1 / (2 * (v - u)).
   *
   * @param value value in [x,y]
   * @param valueInterval value interval [x,y]
   * @param quantizationLevels quantization levels [u,v]
   * @return quantized value in [u,v]
   */
  public int quantize(double value, DoubleInterval valueInterval, IntInterval quantizationLevels) {
    if (!valueInterval.contains(value)) {
      throw new IllegalArgumentException(
          "quantization value %f is not in range [%f, %f]"
              .formatted(value, valueInterval.min(), valueInterval.max()));
    }

    double scale =
        (quantizationLevels.max() - quantizationLevels.min())
            / (valueInterval.max() - valueInterval.min());
    return (int) Math.round((value - valueInterval.min()) * scale) + quantizationLevels.min();
  }

  /**
   * dequantize an int value in the interval [u,v] into a double in the interval [x,y]. the *
   * maximum error of dequantize(quantize(value)) is 1 / (2 * (v - u)).
   *
   * @param value value in [u, v]
   * @param quantizationLevels quantization levels [u,v]
   * @param valueInterval value interval [x,y]
   * @return dequantized value in [x, y]
   */
  public double dequantize(
      int value, IntInterval quantizationLevels, DoubleInterval valueInterval) {
    if (!quantizationLevels.contains(value)) {
      throw new IllegalArgumentException(
          "dequantization value %d is not in range [%d, %d]"
              .formatted(value, quantizationLevels.min(), quantizationLevels.max()));
    }

    double scale =
        (valueInterval.max() - valueInterval.min())
            / (quantizationLevels.max() - quantizationLevels.min());
    return valueInterval.min() + ((value - quantizationLevels.min()) * scale);
  }
}
