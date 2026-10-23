package org.molgenis.vipannotate.format.vcf;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.format.StringView;
import tools.jackson.core.internal.shaded.fdp.JavaDoubleParser;

/** low memory, high performance, reusable, lazy parsing */
public final class Qual extends VcfField {
  private static final char FIELD_RAW_MISSING_VALUE = '.';

  private Qual(StringView fieldRaw) {
    super(fieldRaw);
  }

  @Override
  public boolean isMissingValue() {
    return fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE;
  }

  // perf: parse on demand
  public @Nullable Double getRaw() {
    return isMissingValue() ? null : JavaDoubleParser.parseDouble(fieldRawView);
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
