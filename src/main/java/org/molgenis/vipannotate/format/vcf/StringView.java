package org.molgenis.vipannotate.format.vcf;

public final class StringView implements CharSequence {
  private CharSequence str;
  private int from;
  private int to;

  public StringView(CharSequence str) {
    this(str, 0);
  }

  public StringView(CharSequence str, int from) {
    this(str, from, str.length());
  }

  public StringView(CharSequence str, int from, int to) {
    reset(str, from, to);
  }

  @Override
  public int length() {
    return to - from;
  }

  @Override
  public char charAt(int index) {
    return str.charAt(from + index);
  }

  public int indexOf(char c) {
    int index = indexOf(str, c, from, to);
    return index != -1 ? index - from : -1;
  }

  public int indexOf(char c, int fromIndex) {
    int index = indexOf(str, c, from + fromIndex, to);
    return index != -1 ? index - from : -1;
  }

  public int indexOf(char c, int fromIndex, int toIndex) {
    int index = indexOf(str, c, from + fromIndex, from + toIndex);
    return index != -1 ? index - from : -1;
  }

  public StringView subSequence(int start) {
    return new StringView(str, from + start, to);
  }

  @Override
  public StringView subSequence(int start, int end) {
    return new StringView(str, from + start, from + end);
  }

  public String asString() {
    return str.subSequence(from, to).toString();
  }

  @Override
  public String toString() {
    return asString();
  }

  public void reset(CharSequence str, int from, int to) {
    if (from < 0 || to > str.length() || from > to) {
      throw new IndexOutOfBoundsException();
    }
    this.str = str;
    this.from = from;
    this.to = to;
  }

  public static int indexOf(CharSequence seq, char c) {
    return indexOf(seq, c, 0);
  }

  public static int indexOf(CharSequence seq, char c, int beginIndex) {
    return indexOf(seq, c, beginIndex, seq.length());
  }

  public static int indexOf(CharSequence seq, char c, int beginIndex, int endIndex) {
    for (int i = beginIndex; i < endIndex; i++) {
      if (seq.charAt(i) == c) {
        return i;
      }
    }
    return -1;
  }
}
