package org.molgenis.vipannotate.format.vcf;

/** low memory, high performance, reusable, lazy parsing */
public final class Ref extends Field {
  private Ref(StringView fieldRawView) {
    super(fieldRawView);
  }

  public CharSequence getBases() {
    return fieldRawView;
  }

  public int getBaseCount() {
    return fieldRawView.length();
  }

  public static Ref wrap(String fieldRaw) {
    return Ref.wrap(new StringView(fieldRaw));
  }

  public static Ref wrap(StringView fieldRaw) {
    return new Ref(fieldRaw);
  }

  @Override
  public String toString() {
    return "REF=" + super.toString();
  }
}
