package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class Chrom extends Field {
  private Chrom(StringView fieldRawView) {
    super(fieldRawView);
  }

  public CharSequence get() {
    return fieldRawView;
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
