package org.molgenis.vipannotate.format.bed;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

/** low memory, high performance, reusable, lazy parsing */
public final class Name extends Field {
  private Name(StringView fieldRawView) {
    super(fieldRawView);
  }

  public CharSequence get() {
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
