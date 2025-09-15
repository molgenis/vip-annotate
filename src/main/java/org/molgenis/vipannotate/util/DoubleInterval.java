package org.molgenis.vipannotate.util;

/**
 * Closed double interval
 *
 * @param min min value (inclusive)
 * @param max max value (inclusive)
 */
public record DoubleInterval(double min, double max) {
  public DoubleInterval {
    if (min > max) {
      throw new IllegalArgumentException("min > max");
    }
  }
}
