package org.molgenis.vipannotate.util;

import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class DecimalFormatRegistry {
  @Nullable private static Map<String, DecimalFormat> DECIMAL_FORMATS;

  public static DecimalFormat getDecimalFormat(String pattern) {
    if (DECIMAL_FORMATS == null) {
      DECIMAL_FORMATS = new HashMap<>(); // lazy init
    }

    DecimalFormat decimalFormat = DECIMAL_FORMATS.get(pattern);
    if (decimalFormat == null) {
      decimalFormat = requireNonNull((DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT));
      decimalFormat.applyPattern(pattern);
      DECIMAL_FORMATS.put(pattern, decimalFormat);
    }
    return decimalFormat;
  }
}
