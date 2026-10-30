package org.molgenis.vipannotate.format.tsv;

import java.util.List;
import org.molgenis.vipannotate.format.Field;
import org.molgenis.vipannotate.format.StringView;

public final class TsvField extends Field {
  /// missing value e.g. "", "." or "NA"
  private final String missingValue;

  /// list separator e.g. "," or "&"
  private final char listSeparator;

  private TsvField(StringView fieldRawView, String missingValue, char listSeparator) {
    super(fieldRawView);
    this.missingValue = missingValue;
    this.listSeparator = listSeparator;
  }

  public void parseValuesAppendTo(List<String> values) {
    if (isMissingValue()) {
      return;
    }

    int start = 0;
    int length = fieldRawView.length();
    for (int i = 0; i < length; i++) {
      if (fieldRawView.charAt(i) == listSeparator) {
        values.add(fieldRawView.subSequence(start, i).toString());
        start = i + 1;
      }
    }

    values.add(fieldRawView.subSequence(start, length).toString());
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

  public static TsvField wrap(String fieldRaw, String missingValue, char listSeparator) {
    return TsvField.wrap(new StringView(fieldRaw), missingValue, listSeparator);
  }

  public static TsvField wrap(StringView fieldRaw, String missingValue, char listSeparator) {
    return new TsvField(fieldRaw, missingValue, listSeparator);
  }
}
