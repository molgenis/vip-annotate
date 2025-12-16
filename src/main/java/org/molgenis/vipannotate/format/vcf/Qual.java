package org.molgenis.vipannotate.format.vcf;

import org.jspecify.annotations.Nullable;

/** low memory, high performance, reusable, lazy parsing */
public final class Qual extends Field {
  private static final char FIELD_RAW_MISSING_VALUE = '.';

  private Qual(StringView fieldRaw) {
    super(fieldRaw);
  }

  // perf: parse on demand
  public @Nullable Double get() {
    return fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE
        ? null
        : Double.parseDouble(fieldRawView.asString());
  }

  public static Qual wrap(String fieldRaw) {
    return Qual.wrap(new StringView(fieldRaw));
  }

  public static Qual wrap(StringView fieldRaw) {
    return new Qual(fieldRaw);
  }

  @Override
  public String toString() {
    return "QUAL=" + super.toString();
  }
}
