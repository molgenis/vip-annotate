package org.molgenis.vipannotate.format.vcf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** low memory, high performance, reusable, lazy parsing */
public final class Id extends Field {
  private static final char FIELD_RAW_MISSING_VALUE = '.';
  private static final char FIELD_RAW_SEPARATOR = ';';

  @Nullable private List<CharSequence> parsedField;

  private Id(StringView fieldRawView) {
    super(fieldRawView);
  }

  // perf: parse on demand
  public List<CharSequence> getIdentifiers() {
    parseIfNeeded();
    return parsedField == null ? Collections.emptyList() : parsedField;
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

  public static Id wrap(String fieldRaw) {
    return Id.wrap(new StringView(fieldRaw));
  }

  public static Id wrap(StringView fieldRaw) {
    return new Id(fieldRaw);
  }

  @Override
  public String toString() {
    return "ID=" + super.toString();
  }
}
