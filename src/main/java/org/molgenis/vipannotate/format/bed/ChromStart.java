package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class ChromStart extends Field {
  private int parsedField;

  private ChromStart(StringView fieldRawView) {
    super(fieldRawView);
  }

  public int get() {
    parseIfNeeded();
    return parsedField;
  }

  @Override
  protected void onParse() {
    parsedField = Integer.parseInt(fieldRawView, 0, fieldRawView.length(), 10);
  }

  @Override
  protected void onReset() {
    parsedField = -1;
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
