package org.molgenis.vipannotate.format.vcf;

import lombok.RequiredArgsConstructor;

/** low memory, high performance, reusable, lazy parsing */
@RequiredArgsConstructor
public final class AltAllele {
  private final CharSequence fieldValueRaw;

  public CharSequence get() {
    return fieldValueRaw;
  }

  public AltAlleleType getType() {
    int length = fieldValueRaw.length();

    // handle single-character cases early
    if (length == 1) {
      char c = fieldValueRaw.charAt(0);
      return switch (c) {
        case '.' -> AltAlleleType.MISSING;
        case '*' -> AltAlleleType.MISSING_OVERLAPPING_DELETION;
        default -> AltAlleleType.BASES;
      };
    }

    char firstChar = fieldValueRaw.charAt(0);
    char lastChar = fieldValueRaw.charAt(length - 1);

    // check for "<*>"
    if (length == 3 && firstChar == '<' && fieldValueRaw.charAt(1) == '*' && lastChar == '>') {
      return AltAlleleType.UNSPECIFIED;
    }

    // check symbolic allele
    if (firstChar == '<' && lastChar == '>') {
      return AltAlleleType.SYMBOLIC;
    }

    // check breakend replacement
    if (firstChar == ']' || lastChar == '[') {
      return AltAlleleType.BREAKEND_REPLACEMENT;
    }

    // check single breakend
    if (firstChar == '.' || lastChar == '.') {
      return AltAlleleType.SINGLE_BREAKEND;
    }

    // default
    return AltAlleleType.BASES;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AltAllele other)) return false;

    return CharSequence.compare(this.fieldValueRaw, other.fieldValueRaw) == 0;
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (int i = 0, len = fieldValueRaw.length(); i < len; i++) {
      h = 31 * h + fieldValueRaw.charAt(i);
    }
    return h;
  }

  @Override
  public String toString() {
    return fieldValueRaw.toString();
  }
}
