package org.molgenis.vipannotate.format.vcf;

import java.io.IOException;
import java.io.Writer;
import lombok.RequiredArgsConstructor;

/// VCF field
/// Keywords: Low memory, high performance, reusable, lazy parsing
@RequiredArgsConstructor
public abstract class Field {
  /// Raw unparsed view of the field content
  protected final StringView fieldRawView;

  /// Whether the field has been parsed
  protected boolean parsed;

  /// Resets the field view to point to a new field. Will clear parsed state and
  /// reset internal structures (if any).
  final void reset(String fieldRaw) {
    reset(fieldRaw, 0, fieldRaw.length());
  }

  /// Resets the field to point to a new range in the input string. Will clear parsed state and
  /// reset internal structures (if any).
  final void reset(CharSequence dataLine, int from, int to) {
    fieldRawView.reset(dataLine, from, to);
    if (parsed) {
      parsed = false;
    }
    onReset();
  }

  /// Parses the field if it hasn't already been parsed. Subclasses should override
  /// [#onParse()] to implement parsing logic.
  protected final void parseIfNeeded() {
    if (!parsed) {
      onParse();
      parsed = true;
    }
  }

  /// Called by [#parseIfNeeded()]. Subclasses may override to populate internal state.
  protected void onParse() {
    // default: no-op
  }

  /// Called by [#reset(String,int,int)]. Subclasses may override to clear internal state.
  protected void onReset() {
    // default: no-op
  }

  /// Writes the raw unparsed view. Subclasses may override for custom output behavior.
  public void write(Writer writer) throws IOException {
    writer.append(fieldRawView);
  }

  @Override
  public String toString() {
    return fieldRawView.toString();
  }
}
