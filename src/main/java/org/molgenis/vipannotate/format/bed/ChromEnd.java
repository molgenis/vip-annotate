package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class ChromEnd extends BedField {
  private long parsedField;

  private ChromEnd(StringView fieldRawView) {
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

  public static ChromEnd wrap(String fieldRaw) {
    return ChromEnd.wrap(new StringView(fieldRaw));
  }

  public static ChromEnd wrap(StringView fieldRaw) {
    return new ChromEnd(fieldRaw);
  }

  @Override
  public String toString() {
    return "CHROM_END=" + super.toString();
  }
}
