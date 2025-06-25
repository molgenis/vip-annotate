package org.molgenis.vipannotate.util;

public class Quantizer {
  /**
   * quantize a double value in the interval [x,y] into an integer in the interval [u, v]. the
   * maximum error of dequantize(quantize(value)) is 1 / (2 * (v - u)).
   *
   * @param value value in [x, y]
   * @param x x in [x, y]
   * @param y y in [x, y]
   * @param u u in [u, v]
   * @param v v in [u, v]
   * @return quantized value in [u, v]
   */
  public int quantize(double value, double x, double y, int u, int v) {
    requireInterval(x, y);
    requireInterval(u, v);
    if (value < x || value > y) {
      throw new IllegalArgumentException();
    }

    double scale = (v - u) / (y - x);
    return (int) Math.round((value - x) * scale) + u;
  }

  /**
   * dequantize an int value in the interval [u,v] into a double in the interval [x,y]. the *
   * maximum error of dequantize(quantize(value)) is 1 / (2 * (v - u)).
   *
   * @param value value in [u, v]
   * @param u u in [u, v]
   * @param v v in [u, v]
   * @param x x in [x, y]
   * @param y y in [x, y]
   * @return dequantized value in [x, y]
   */
  public double dequantize(int value, int u, int v, double x, double y) {
    requireInterval(x, y);
    requireInterval(u, v);
    if (value < u || value > v) {
      throw new IllegalArgumentException(
          "dequantization value %d is not in range [%d, %d]".formatted(value, u, v));
    }

    double scale = (y - x) / (v - u);
    return x + ((value - u) * scale);
  }

  private static void requireInterval(double x, double y) {
    if (y < x) {
      throw new IllegalArgumentException();
    }
  }

  private static void requireInterval(int x, int y) {
    if (y < x) {
      throw new IllegalArgumentException();
    }
  }
}
