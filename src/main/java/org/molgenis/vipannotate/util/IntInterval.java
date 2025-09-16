package org.molgenis.vipannotate.util;

/**
 * Closed int interval
 *
 * @param min min value (inclusive)
 * @param max max value (inclusive)
 */
public record IntInterval(int min, int max) {
  public IntInterval {
    if (min > max) {
      throw new IllegalArgumentException("min > max");
    }
  }

  public boolean contains(int value) {
    return value >= min && value <= max;
  }
}
