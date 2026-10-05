package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class ChromStart extends BedField {
  private long parsedField;

  private ChromStart(StringView fieldRawView) {
    super(fieldRawView);
  }

  @Override
  public boolean isMissingValue() {
    return false;
  }

  public long getRaw() {
    parseIfNeeded();
    return parsedField;
  }

  @Override
  protected void onParse() {
    parsedField = Long.parseLong(fieldRawView, 0, fieldRawView.length(), 10);
  }

  @Override
  protected void onReset() {
    parsedField = -1L;
  }

  public static ChromStart wrap(String fieldRaw) {
    return ChromStart.wrap(new StringView(fieldRaw));
  }

  public static ChromStart wrap(StringView fieldRaw) {
    return new ChromStart(fieldRaw);
  }

  @Override
  public String toString() {
    return "CHROM_START=" + super.toString();
  }
}
