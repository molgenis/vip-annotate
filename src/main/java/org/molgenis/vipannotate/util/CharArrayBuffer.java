package org.molgenis.vipannotate.util;

import static org.molgenis.vipannotate.util.Numbers.requirePositive;

import java.util.Arrays;
import lombok.Getter;
import lombok.NonNull;

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

  public void append(@NonNull String s) {
    int strLen = s.length();
    ensureCapacity(length + strLen);
    s.getChars(0, strLen, buffer, length);
    length += strLen;
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
