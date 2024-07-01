package org.molgenis.vipannotate.format.vcf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** low memory, high performance, reusable, lazy parsing */
public final class Filter extends Field {
  private static final char FIELD_RAW_MISSING_VALUE = '.';
  private static final char FIELD_RAW_SEPARATOR = ';';

  @Nullable private List<CharSequence> parsedField;

  private Filter(StringView fieldRaw) {
    super(fieldRaw);
  }

  public List<CharSequence> getCodes() {
    parseIfNeeded();
    return parsedField == null ? Collections.emptyList() : parsedField;
  }

  public boolean isPass() {
    return parsedField != null
        ? parsedField.size() == 1 && CharSequence.compare("PASS", parsedField.getFirst()) == 0
        : CharSequence.compare("PASS", fieldRawView) == 0;
  }

  @Override
  protected void onParse() {
    // fast path: missing value
    if (fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE) {
      return;
    }

    if (parsedField == null) {
      parsedField = new ArrayList<>(1);
    }

    // single identifier
    int start = fieldRawView.indexOf(FIELD_RAW_SEPARATOR);
    if (start == -1) {
      parsedField.addFirst(fieldRawView);
      return;
    }

    // multiple identifiers
    parsedField.addFirst(fieldRawView.subSequence(0, start));
    start = start + 1;

    int end;
    do {
      end = fieldRawView.indexOf(FIELD_RAW_SEPARATOR, start);
      if (end == -1) {
        parsedField.add(fieldRawView.subSequence(start));
        break;
      }
      parsedField.add(fieldRawView.subSequence(start, end));
      start = end + 1;
    } while (true);
  }

  @Override
  protected void onReset() {
    if (parsedField != null) {
      parsedField.clear();
    }
  }

  public static Filter wrap(String fieldRaw) {
    return Filter.wrap(new StringView(fieldRaw));
  }

  public static Filter wrap(StringView fieldRaw) {
    return new Filter(fieldRaw);
  }

  @Override
  public String toString() {
    return "FILTER=" + super.toString();
  }
}
