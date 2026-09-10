package org.molgenis.vipannotate.util;

/**
 * Closed integer interval
 *
 * @param min min value (inclusive)
 * @param max max value (inclusive)
 */
public record IntInterval(long min, long max) {
  public IntInterval {
    if (min > max) {
      throw new IllegalArgumentException("min > max");
    }
  }

  public boolean contains(long value) {
    return value >= min && value <= max;
  }
}
