package org.molgenis.vipannotate.format.vcf;

/** low memory, high performance, reusable, lazy parsing */
public final class Pos extends Field {
  private int parsedField;

  private Pos(StringView fieldRawView) {
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

  public static Pos wrap(String fieldRaw) {
    return Pos.wrap(new StringView(fieldRaw));
  }

  public static Pos wrap(StringView fieldRaw) {
    return new Pos(fieldRaw);
  }

  @Override
  public String toString() {
    return "POS=" + super.toString();
  }
}
