package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class ChromEnd extends Field {
  private int parsedField;

  private ChromEnd(StringView fieldRawView) {
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
