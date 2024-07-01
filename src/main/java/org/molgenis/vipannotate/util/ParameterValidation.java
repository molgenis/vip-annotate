package org.molgenis.vipannotate.util;

public class ParameterValidation {
  public static int requirePositive(int num) {
    if (num <= 0) throw new IllegalArgumentException("number must be positive");
    return num;
  }
}
