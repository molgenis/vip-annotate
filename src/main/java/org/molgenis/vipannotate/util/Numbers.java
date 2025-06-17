package org.molgenis.vipannotate.util;

public class Numbers {
  public static void validatePositive(int num) {
    requirePositive(num);
  }

  public static int requirePositive(int num) {
    if (num <= 0) throw new IllegalArgumentException("number must be positive");
    return num;
  }

  public static void validatePositive(long num) {
    requirePositive(num);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static long requirePositive(long num) {
    if (num <= 0) throw new IllegalArgumentException("number must be positive");
    return num;
  }

  public static void validateNonNegative(double num) {
    requireNonNegative(num);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static double requireNonNegative(double num) {
    if (num < 0) {
      throw new IllegalArgumentException("number must be greater than or equal to zero");
    }
    return num;
  }

  public static void validateNonNegativeOrNull(Double num) {
    //noinspection ResultOfMethodCallIgnored
    requireNonNegativeOrNull(num);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static Double requireNonNegativeOrNull(Double num) {
    if (num != null && num < 0) {
      throw new IllegalArgumentException("number must be null or greater than or equal to zero");
    }
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

  public static long requireNonNegative(long num) {
    if (num < 0) {
      throw new IllegalArgumentException("number must be greater than or equal to zero");
    }
    return num;
  }

  public static void validateNonNegativeOrNull(Integer num) {
    //noinspection ResultOfMethodCallIgnored
    requireNonNegativeOrNull(num);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static Integer requireNonNegativeOrNull(Integer num) {
    if (num != null && num < 0) {
      throw new IllegalArgumentException("number must be null or greater than or equal to zero");
    }
    return num;
  }
}
