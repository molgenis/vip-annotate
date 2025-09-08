package org.molgenis.vipannotate.util;

import org.jspecify.annotations.Nullable;

public class Numbers {
  public static void validatePositive(int num) {
    requirePositive(num);
  }

  public static int requirePositive(int num) {
    if (num <= 0) throw new IllegalArgumentException("number must be positive");
    return num;
  }

  public static void validateNonNegative(int num) {
    requireNonNegative(num);
  }

  public static int requireNonNegative(int num) {
    if (num < 0) {
      throw new IllegalArgumentException("number must be greater than or equal to zero");
    }
    return num;
  }

  public static void validateNonNegative(long num) {
    requireNonNegative(num);
  }

  public static long requireNonNegative(long num) {
    if (num < 0) {
      throw new IllegalArgumentException("number must be greater than or equal to zero");
    }
    return num;
  }

  public static void validatePositive(long num) {
    requirePositive(num);
  }

  public static long requirePositive(long num) {
    if (num <= 0) throw new IllegalArgumentException("number must be positive");
    return num;
  }

  public static void validateNonNegative(double num) {
    requireNonNegative(num);
  }

  public static double requireNonNegative(double num) {
    if (num < 0) {
      throw new IllegalArgumentException("number must be greater than or equal to zero");
    }
    return num;
  }

  public static void validateNonNegativeOrNull(@Nullable Double num) {
    //noinspection ResultOfMethodCallIgnored
    requireNonNegativeOrNull(num);
  }

  public static @Nullable Double requireNonNegativeOrNull(@Nullable Double num) {
    if (num != null && num < 0) {
      throw new IllegalArgumentException("number must be null or greater than or equal to zero");
    }
    return num;
  }

  public static void validateNonNegativeOrNull(@Nullable Integer num) {
    //noinspection ResultOfMethodCallIgnored
    requireNonNegativeOrNull(num);
  }

  public static @Nullable Integer requireNonNegativeOrNull(@Nullable Integer num) {
    if (num != null && num < 0) {
      throw new IllegalArgumentException("number must be null or greater than or equal to zero");
    }
    return num;
  }

  public static void validateUnitInterval(double num) {
    if (num < 0 || num > 1) {
      throw new IllegalArgumentException("number must be in range [0, 1]");
    }
  }
}
