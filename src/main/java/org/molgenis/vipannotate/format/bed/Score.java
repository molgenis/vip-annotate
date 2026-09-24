package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class Score extends BedField {
  private Score(StringView fieldRawView) {
    super(fieldRawView);
  }

  public CharSequence getRaw() {
    return fieldRawView;
  }

  public static Score wrap(String fieldRaw) {
    return Score.wrap(new StringView(fieldRaw));
  }

  public static Score wrap(StringView fieldRaw) {
    return new Score(fieldRaw);
  }

  @Override
  public String toString() {
    return "SCORE=" + super.toString();
  }
}
