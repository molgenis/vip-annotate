package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.Numbers.requirePositive;

import java.util.Arrays;
import lombok.Getter;

public class CharArrayBuffer {
  @Getter private char[] buffer;
  @Getter private int length;

  public CharArrayBuffer(int initialCapacity) {
    requirePositive(initialCapacity);
    this.buffer = new char[initialCapacity];
    this.length = 0;
  }

  public void append(char c) {
    ensureCapacity(length + 1);
    buffer[length++] = c;
  }

  public void append(char[] chars) {
    append(chars, 0, chars.length);
  }

  public void append(char[] chars, int off, int len) {
    ensureCapacity(length + len);
    System.arraycopy(chars, off, buffer, length, len);
  }

  public void append(CharSequence charSequence) {
    append(charSequence, 0, charSequence.length());
  }

  public void append(CharSequence charSequence, int off, int len) {
    ensureCapacity(length + len);
    charSequence.getChars(off, len, buffer, length);
    length += len;
  }

  public void clear() {
    length = 0;
  }

  private void ensureCapacity(int minCapacity) {
    if (minCapacity > buffer.length) {
      int newCapacity = Math.max(buffer.length * 2, minCapacity);
      buffer = Arrays.copyOf(buffer, newCapacity);
    }
  }

  @Override
  public String toString() {
    return new String(buffer, 0, length);
  }
}
