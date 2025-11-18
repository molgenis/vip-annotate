package org.molgenis.vipannotate.format.vcf;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/** low memory, high performance, reusable, lazy parsing */
public final class Alt extends Field {
  private static final char FIELD_RAW_MISSING_VALUE = '.';
  private static final char FIELD_RAW_SEPARATOR = ',';

  @Nullable private List<AltAllele> parsedField;

  private Alt(StringView fieldRaw) {
    super(fieldRaw);
  }

  /**
   * Returns the first alternate allele.
   *
   * @throws IllegalArgumentException in case of no alt alleles
   */
  @Deprecated
  public AltAllele getFirstAllele() {
    // TODO update getAlleles() to return new 'AltAlleles' with getCount/getByIndex to delay parsing
    AltAllele altAllele;
    if (fieldRawView.length() == 1) {
      if (fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE) {
        throw new IllegalArgumentException("no alt allele");
      }
      // fast path: prevent parsing
      altAllele = AltAlleleRegistry.INSTANCE.get(fieldRawView);
    } else if (fieldRawView.length() == 2) {
      // fast path: prevent parsing
      altAllele = AltAlleleRegistry.INSTANCE.get(fieldRawView);
    } else {
      altAllele = getAlleles().getFirst();
    }
    return altAllele;
  }

  @SuppressWarnings({"DataFlowIssue", "NullAway"})
  public List<AltAllele> getAlleles() {
    parseIfNeeded();
    return parsedField;
  }

  @Override
  protected void onParse() {
    if (parsedField == null) {
      parsedField = new ArrayList<>(2);
    }
    if (fieldRawView.length() == 1 && fieldRawView.charAt(0) == FIELD_RAW_MISSING_VALUE) {
      // fast path: missing value
      return;
    }

    int commaIndex = fieldRawView.indexOf(FIELD_RAW_SEPARATOR);
    if (commaIndex == -1) {
      // fast path: single alt allele
      parsedField.addFirst(AltAlleleRegistry.INSTANCE.get(fieldRawView));
      return;
    }

    int start = 0;
    int comma = commaIndex;
    while (comma != -1) {
      parsedField.add(AltAlleleRegistry.INSTANCE.get(fieldRawView.subSequence(start, comma)));
      start = comma + 1;
      comma = fieldRawView.indexOf(FIELD_RAW_SEPARATOR, start);
    }
    parsedField.add(AltAlleleRegistry.INSTANCE.get(fieldRawView.subSequence(start)));
  }

  @Override
  protected void onReset() {
    if (parsedField != null) {
      parsedField.clear();
    }
  }

  public static Alt wrap(String fieldRaw) {
    return Alt.wrap(new StringView(fieldRaw));
  }

  public static Alt wrap(StringView fieldRaw) {
    return new Alt(fieldRaw);
  }

  @Override
  public String toString() {
    return "ALT=" + super.toString();
  }
}
