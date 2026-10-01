package org.molgenis.vipannotate.format.tsv;

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

  public String[] parseValues() {
    int length = fieldRawView.length();
    // fast path: empty list for missing value
    if (isMissingValue()) {
      return new String[0];
    }

    for (int i = 0; i < length; i++) {
      if (fieldRawView.charAt(i) == listSeparator) {
        // multiple enum values
        int tokenCount = 2;

        for (int j = i + 1; j < length; j++) {
          if (fieldRawView.charAt(j) == listSeparator) {
            tokenCount++;
          }
        }

        String[] tokens = new String[tokenCount];
        int tokenIndex = 0;
        int start = 0;

        for (int j = 0; j < length; j++) {
          if (fieldRawView.charAt(j) == listSeparator) {
            tokens[tokenIndex++] = fieldRawView.subSequence(start, j).toString();
            start = j + 1;
          }
        }

        tokens[tokenIndex] = fieldRawView.subSequence(start, length).toString();
        return tokens;
      }
    }

    // fast path: one enum value
    return new String[] {fieldRawView.toString()};
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
