package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/// Mutable representation of a VCF INFO field.
public final class Info extends Field {
  private static final char FIELD_RAW_MISSING_VALUE = '.';

  @Nullable private StringBuilder fieldRawAppendBuilder;
  @Nullable private Map<String, @Nullable CharSequence> parsedField;

  private Info(StringView fieldRaw) {
    super(fieldRaw);
  }

  /**
   * Returns raw info value
   *
   * @throws IllegalArgumentException if info field could have been modified
   */
  // TODO remove temporary method that improves performance of read-only info single token gets
  @Deprecated
  public CharSequence getRaw(int fromIndex) {
    if (parsed) throw new IllegalArgumentException();
    return fieldRawView.subSequence(fromIndex);
  }

  @SuppressWarnings("NullAway")
  public @Nullable CharSequence get(String key) {
    parseIfNeeded();
    return parsedField.get(key);
  }

  @SuppressWarnings("NullAway")
  public void append(String key) {
    prepareAppend();
    fieldRawAppendBuilder.append(key);
  }

  @SuppressWarnings("NullAway")
  public void append(String key, CharSequence subfieldRawValue) {
    prepareAppend();
    fieldRawAppendBuilder.append(key).append('=').append(subfieldRawValue);
  }

  private void prepareAppend() {
    if (fieldRawAppendBuilder == null) {
      fieldRawAppendBuilder = new StringBuilder();
    } else if (!fieldRawAppendBuilder.isEmpty()) {
      fieldRawAppendBuilder.append(';');
    }
  }

  @SuppressWarnings("NullAway")
  public void put(String key) {
    parseIfNeeded();
    this.parsedField.put(key, null);
  }

  @SuppressWarnings("NullAway")
  public void put(String key, CharSequence subfieldRawValue) {
    parseIfNeeded();
    this.parsedField.put(key, subfieldRawValue.toString());
  }

  @SuppressWarnings("NullAway")
  public void remove(String key) {
    parseIfNeeded();
    this.parsedField.remove(key);
  }

  @Override
  public void write(Writer writer) throws IOException {
    if (parsed) {
      writeParsed(writer);
    } else {
      writeAppended(writer);
    }
  }

  @SuppressWarnings("NullAway")
  private void writeParsed(Writer writer) throws IOException {
    if (!parsedField.isEmpty()) {
      boolean writeSeparator = false;
      for (Map.Entry<String, @Nullable CharSequence> entry : parsedField.entrySet()) {
        if (writeSeparator) {
          writer.append(';');
        } else {
          writeSeparator = true;
        }
        writer.append(entry.getKey());
        if (entry.getValue() != null) {
          //noinspection DataFlowIssue
          writer.append('=').append(entry.getValue());
        }
      }
    } else {
      writer.append(FIELD_RAW_MISSING_VALUE);
    }
  }

  private void writeAppended(Writer writer) throws IOException {
    if (fieldRawAppendBuilder != null) {
      if (!(fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE)) {
        super.write(writer);
        writer.append(';');
      }
      writer.append(fieldRawAppendBuilder);
    } else {
      super.write(writer);
    }
  }

  public static Info wrap() {
    return Info.wrap(Character.toString(FIELD_RAW_MISSING_VALUE));
  }

  public static Info wrap(String fieldRaw) {
    return Info.wrap(new StringView(fieldRaw));
  }

  public static Info wrap(StringView fieldRaw) {
    return new Info(fieldRaw);
  }

  @Override
  protected void onParse() {
    if (parsedField == null) {
      parsedField = new LinkedHashMap<>();
    }
    if (!(fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE)) {
      for (int start = 0, length = fieldRawView.length(); start < length; ) {
        int end = fieldRawView.indexOf(';', start);
        if (end == -1) {
          end = length;
        }

        int eq = fieldRawView.indexOf('=', start);
        if (eq != -1 && eq < end) {
          // key=value
          String key = fieldRawView.subSequence(start, eq).toString();
          CharSequence subfieldRawValue = fieldRawView.subSequence(eq + 1, end);
          parsedField.put(key, subfieldRawValue);
        } else {
          // flag
          String key = fieldRawView.subSequence(start, end).toString();
          parsedField.put(key, null);
        }

        start = end + 1;
      }
    }
  }

  @Override
  protected void onReset() {
    if (fieldRawAppendBuilder != null) {
      fieldRawAppendBuilder.setLength(0);
    }
    if (parsedField != null) {
      parsedField.clear();
    }
  }

  @Override
  public String toString() {
    return "INFO="
        + (parsed
            ? parsedField
            : (fieldRawView
                + (fieldRawAppendBuilder != null ? ",append=" + fieldRawAppendBuilder : "")));
  }
}
