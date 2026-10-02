package org.molgenis.vipannotate.format.tsv;

import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public final class TsvField extends Field {
  /// missing value e.g. "", "." or "NA"
  private final String missingValue;

  private TsvField(StringView fieldRawView, String missingValue) {
    super(fieldRawView);
    this.missingValue = missingValue;
  }

  public CharSequence getRaw() {
    return fieldRawView;
  }

  @Override
  public boolean isMissingValue() {
    return switch (missingValue.length()) {
      case 0 -> fieldRawView.isEmpty();
      case 1 -> fieldRawView.length() == 1 && fieldRawView.charAt(0) == missingValue.charAt(0);
      default -> {
        for (int i = 0; i < missingValue.length(); i++) {
          if (fieldRawView.charAt(i) != missingValue.charAt(i)) yield false;
        }
        yield true;
      }
    };
  }

  public static TsvField wrap(String fieldRaw, String missingValue) {
    return TsvField.wrap(new StringView(fieldRaw), missingValue);
  }

  public static TsvField wrap(StringView fieldRaw, String missingValue) {
    return new TsvField(fieldRaw, missingValue);
  }
}
