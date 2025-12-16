package org.molgenis.vipannotate.format.vcf;

/** low memory, high performance, reusable, lazy parsing */
public final class Chrom extends Field {
  private Chrom(StringView fieldRawView) {
    super(fieldRawView);
  }

  // perf: parse on demand
  /// Returns chromosome identifier, with optional angle-brackets removed
  public CharSequence getIdentifier() {
    return getType() == ChromType.SYMBOLIC
        ? fieldRawView.subSequence(1, fieldRawView.length() - 1)
        : fieldRawView;
  }

  // perf: parse on demand
  /// Returns chromosome identifier type
  public ChromType getType() {
    return fieldRawView.charAt(0) == '<' && fieldRawView.charAt(fieldRawView.length() - 1) == '>'
        ? ChromType.SYMBOLIC
        : ChromType.IDENTIFIER;
  }

  public static Chrom wrap(String fieldRaw) {
    return Chrom.wrap(new StringView(fieldRaw));
  }

  public static Chrom wrap(StringView fieldRaw) {
    return new Chrom(fieldRaw);
  }

  @Override
  public String toString() {
    return "CHROM=" + super.toString();
  }
}
