package org.molgenis.vipannotate.format.tsv;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public final class TsvField extends Field {
  private TsvField(StringView fieldRawView) {
    super(fieldRawView);
  }

  public CharSequence getRaw() {
    return fieldRawView;
  }

  public static TsvField wrap(String fieldRaw) {
    return TsvField.wrap(new StringView(fieldRaw));
  }

  public static TsvField wrap(StringView fieldRaw) {
    return new TsvField(fieldRaw);
  }
}
