package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class Name extends BedField {
  private static final char UNINFORMATIVE_VALUE = '.';

  private Name(StringView fieldRawView) {
    super(fieldRawView);
  }

  @Override
  public boolean isMissingValue() {
    return fieldRawView.length() == 1 && fieldRawView.charAt(0) == UNINFORMATIVE_VALUE;
  }

  public CharSequence getRaw() {
    return fieldRawView;
  }

  public static Name wrap(String fieldRaw) {
    return Name.wrap(new StringView(fieldRaw));
  }

  public static Name wrap(StringView fieldRaw) {
    return new Name(fieldRaw);
  }

  @Override
  public String toString() {
    return "NAME=" + super.toString();
  }
}
