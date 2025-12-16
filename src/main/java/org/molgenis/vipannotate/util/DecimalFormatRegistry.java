package org.molgenis.vipannotate.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** {@link DecimalFormat} registry singleton */
@SuppressWarnings("ImmutableEnumChecker")
public enum DecimalFormatRegistry {
  INSTANCE;

  private final Map<String, DecimalFormat> decimalFormats = new HashMap<>();

  /**
   * Returns a {@link DecimalFormat} for the given pattern.
   *
   * @param pattern a root-locale pattern string
   * @return {@code DecimalFormat} using strict parsing and half up rounding
   */
  public DecimalFormat get(String pattern) {
    DecimalFormat decimalFormat = decimalFormats.get(pattern);
    if (decimalFormat == null) {
      decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
      //noinspection DataFlowIssue
      decimalFormat.setStrict(true);
      decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
      decimalFormat.applyPattern(pattern);
      decimalFormats.put(pattern, decimalFormat);
    }
    return decimalFormat;
  }
}
