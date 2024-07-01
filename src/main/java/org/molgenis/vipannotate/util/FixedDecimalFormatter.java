package org.molgenis.vipannotate.util;

import java.text.DecimalFormat;

/**
 * Fast alternatives for:
 *
 * <pre>{@code
 * DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
 * decimalFormat.setStrict(true);
 * decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
 * decimalFormat.applyPattern(<pattern>);
 * decimalFormat.format(value)
 * }</pre>
 */
public final class FixedDecimalFormatter {

  private FixedDecimalFormatter() {}

  /** Fast alternative for {@link DecimalFormat} with pattern <code>#</code>. */
  public static void appendFixed0(StringBuilder sb, double value) {
    if (value < 0) {
      sb.append('-');
      value = -value;
    }

    long rounded = Math.round(value);
    sb.append(rounded);
  }

  /** Fast alternative for {@link DecimalFormat} with pattern <code>#.#</code>. */
  public static void appendFixed1(StringBuilder sb, double value) {
    if (value < 0) {
      sb.append('-');
      value = -value;
    }

    long scaled = Math.round(value * 10);
    long intPart = scaled / 10;
    int frac = (int) (scaled - intPart * 10);

    sb.append(intPart);
    if (frac != 0) {
      sb.append('.');
      sb.append(frac);
    }
  }

  /** Fast alternative for {@link DecimalFormat} with pattern <code>#.##</code>. */
  public static void appendFixed2(StringBuilder sb, double value) {
    if (value < 0) {
      sb.append('-');
      value = -value;
    }

    long scaled = Math.round(value * 100);
    long intPart = scaled / 100;
    int frac = (int) (scaled - intPart * 100);

    sb.append(intPart);
    if (frac != 0) {
      sb.append('.');
      if (frac % 10 == 0) {
        sb.append(frac / 10);
      } else if (frac < 10) {
        sb.append('0').append(frac);
      } else {
        sb.append(frac);
      }
    }
  }

  /** Fast alternative for {@link DecimalFormat} with pattern <code>#.###</code>. */
  public static void appendFixed3(StringBuilder sb, double value) {
    if (value < 0) {
      sb.append('-');
      value = -value;
    }

    long scaled = Math.round(value * 1000);
    long intPart = scaled / 1000;
    int frac = (int) (scaled - intPart * 1000);

    sb.append(intPart);
    if (frac != 0) {
      sb.append('.');
      if (frac % 100 == 0) {
        sb.append(frac / 100);
      } else if (frac % 10 == 0) {
        int trimmed = frac / 10;
        if (trimmed < 10) {
          sb.append('0');
        }
        sb.append(trimmed);
      } else {
        if (frac < 100) {
          sb.append('0');
        }
        if (frac < 10) {
          sb.append('0');
        }
        sb.append(frac);
      }
    }
  }

  /** Fast alternative for {@link DecimalFormat} with pattern <code>#.####</code>. */
  public static void appendFixed4(StringBuilder sb, double value) {
    if (value < 0) {
      sb.append('-');
      value = -value;
    }

    long scaled = Math.round(value * 10000);
    long intPart = scaled / 10000;
    int frac = (int) (scaled - intPart * 10000);

    sb.append(intPart);
    if (frac != 0) {
      sb.append('.');
      if (frac % 1000 == 0) {
        sb.append(frac / 1000);
      } else if (frac % 100 == 0) {
        int trimmed = frac / 100;
        if (trimmed < 10) {
          sb.append('0');
        }
        sb.append(trimmed);
      } else if (frac % 10 == 0) {
        int trimmed = frac / 10;
        if (trimmed < 100) {
          sb.append('0');
        }
        if (trimmed < 10) {
          sb.append('0');
        }
        sb.append(trimmed);
      } else {
        if (frac < 1000) {
          sb.append('0');
        }
        if (frac < 100) {
          sb.append('0');
        }
        if (frac < 10) {
          sb.append('0');
        }
        sb.append(frac);
      }
    }
  }
}
