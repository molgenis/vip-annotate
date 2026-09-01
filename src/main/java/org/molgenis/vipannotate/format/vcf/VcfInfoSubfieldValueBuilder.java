package org.molgenis.vipannotate.format.vcf;

import org.molgenis.vipannotate.util.FixedDecimalFormatter;

/**
 * vcf info subfield value builder.
 *
 * <p>zero-copy and reusable.
 */
public final class VcfInfoSubfieldValueBuilder {
  private static final char INFO_VALUE_SEPARATOR = ',';
  private static final char INFO_VALUE_MISSING = '.';
  private static final char COMPOSITE_VALUE_SEPARATOR = '|';

  private final StringBuilder stringBuilder;
  private int nrValues;
  private int nrValuesMissing;

  public VcfInfoSubfieldValueBuilder() {
    stringBuilder = new StringBuilder();
    nrValues = 0;
    nrValuesMissing = 0;
  }

  public void appendValue(char c) {
    startRawValue();
    appendRaw(c);
    endRawValue();
  }

  public void appendValue(String str) {
    startRawValue();
    appendRaw(str);
    endRawValue();
  }

  public void appendValue(int i) {
    startRawValue();
    appendRaw(i);
    endRawValue();
  }

  public void appendValue(double d, int nrDecimals) {
    validateNrDecimals(nrDecimals);
    startRawValue();
    appendRaw(d, nrDecimals);
    endRawValue();
  }

  public void appendValueMissing() {
    startRawValue();
    appendRawMissing();
    nrValuesMissing++;
    endRawValue();
  }

  /** Indicates the start of a new top-level value. */
  public void startRawValue() {
    if (nrValues > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
  }

  /**
   * Appends the separator between components of a composite value.
   *
   * <p>This does not affect the number of top-level values.
   */
  public void appendCompositeValueSeparator() {
    stringBuilder.append(COMPOSITE_VALUE_SEPARATOR);
  }

  /**
   * Writes raw data as part of the current top-level value.
   *
   * <p>Must be used between {@link #startRawValue()} and {@link #endRawValue()}.
   */
  public void appendRaw(String str) {
    stringBuilder.append(str);
  }

  /**
   * Writes raw data as part of the current top-level value.
   *
   * <p>Must be used between {@link #startRawValue()} and {@link #endRawValue()}.
   */
  public void appendRaw(char c) {
    stringBuilder.append(c);
  }

  /**
   * Writes raw data as part of the current top-level value.
   *
   * <p>Must be used between {@link #startRawValue()} and {@link #endRawValue()}.
   */
  public void appendRaw(int i) {
    stringBuilder.append(i);
  }

  /**
   * Writes a missing component as part of the current top-level value.
   *
   * <p>Unlike {@link #appendValueMissing()}, this does not increment the number of missing
   * top-level values.
   */
  public void appendRawMissing() {
    stringBuilder.append(INFO_VALUE_MISSING);
  }

  /**
   * Writes raw data as part of the current top-level value.
   *
   * <p>Must be used between {@link #startRawValue()} and {@link #endRawValue()}.
   */
  public void appendRaw(double d, int nrDecimals) {
    validateNrDecimals(nrDecimals);

    switch (nrDecimals) {
      case 0 -> FixedDecimalFormatter.appendFixed0(stringBuilder, d);
      case 1 -> FixedDecimalFormatter.appendFixed1(stringBuilder, d);
      case 2 -> FixedDecimalFormatter.appendFixed2(stringBuilder, d);
      case 3 -> FixedDecimalFormatter.appendFixed3(stringBuilder, d);
      case 4 -> FixedDecimalFormatter.appendFixed4(stringBuilder, d);
    }
  }

  /** Indicates the end of a top-level raw value. */
  public void endRawValue() {
    nrValues++;
  }

  public boolean isEmptyValue() {
    return nrValues == nrValuesMissing;
  }

  public CharSequence build() {
    return stringBuilder;
  }

  public void reset() {
    stringBuilder.setLength(0);
    nrValues = 0;
    nrValuesMissing = 0;
  }

  private static void validateNrDecimals(int nrDecimals) {
    if (nrDecimals < 0 || nrDecimals > 4) {
      throw new IllegalArgumentException("nrDecimals must be between 0 and 4");
    }
  }
}
