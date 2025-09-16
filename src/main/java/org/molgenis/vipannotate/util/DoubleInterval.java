package org.molgenis.vipannotate.util;

/**
 * Closed double interval
 *
 * @param min min value (inclusive)
 * @param max max value (inclusive)
 */
public record DoubleInterval(double min, double max) {
  public static final DoubleInterval UNIT = new DoubleInterval(0d, 1d);

  public DoubleInterval {
    if (min > max) {
      throw new IllegalArgumentException("min > max");
    }
  }

  public boolean contains(double value) {
    return value >= min && value <= max;
  }
}
