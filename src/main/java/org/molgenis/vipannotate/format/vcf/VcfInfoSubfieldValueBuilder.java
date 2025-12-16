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

  private final StringBuilder stringBuilder;
  private int nrValues;
  private int nrValuesMissing;

  public VcfInfoSubfieldValueBuilder() {
    stringBuilder = new StringBuilder();
    nrValues = 0;
    nrValuesMissing = 0;
  }

  public void appendValue(char c) {
    if (nrValues++ > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
    appendRaw(c);
  }

  public void appendValue(String str) {
    if (nrValues++ > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
    appendRaw(str);
  }

  public void appendValue(int i) {
    if (nrValues++ > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
    appendRaw(i);
  }

  public void appendValue(double d, int nrDecimals) {
    if (nrDecimals < 0 || nrDecimals > 4) {
      throw new IllegalArgumentException("nrDecimals must be between 0 and 4");
    }
    if (nrValues++ > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
    appendRaw(d, nrDecimals);
  }

  public void appendValueMissing() {
    if (nrValues++ > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
    stringBuilder.append(INFO_VALUE_MISSING);
    nrValuesMissing++;
  }

  /** indicate start of raw data appending */
  public void startRawValue() {
    if (nrValues > 0) {
      stringBuilder.append(INFO_VALUE_SEPARATOR);
    }
  }

  /**
   * write raw data. use {@link #startRawValue()} and {@link #endRawValue()} before/after writes.
   */
  public void appendRaw(String str) {
    stringBuilder.append(str);
  }

  /**
   * write raw data. use {@link #startRawValue()} and {@link #endRawValue()} before/after writes.
   */
  public void appendRaw(char c) {
    stringBuilder.append(c);
  }

  /**
   * write raw data. use {@link #startRawValue()} and {@link #endRawValue()} before/after writes.
   */
  public void appendRaw(int i) {
    stringBuilder.append(i);
  }

  /**
   * write raw data. use {@link #startRawValue()} and {@link #endRawValue()} before/after writes.
   */
  public void appendRaw(double d, int nrDecimals) {
    switch (nrDecimals) {
      case 0 -> FixedDecimalFormatter.appendFixed0(stringBuilder, d);
      case 1 -> FixedDecimalFormatter.appendFixed1(stringBuilder, d);
      case 2 -> FixedDecimalFormatter.appendFixed2(stringBuilder, d);
      case 3 -> FixedDecimalFormatter.appendFixed3(stringBuilder, d);
      case 4 -> FixedDecimalFormatter.appendFixed4(stringBuilder, d);
    }
  }

  /** indicate end of raw data appending */
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
}
