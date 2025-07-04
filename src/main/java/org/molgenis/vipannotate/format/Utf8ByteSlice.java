package org.molgenis.vipannotate.format;

import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Utf8ByteSlice implements CharSequence {
  private final byte[] byteArray;
  @Setter private int offset;
  @Setter private int length;

  @Override
  public int length() {
    return length;
  }

  @Override
  public char charAt(int index) {
    // UTF-8 chars can be multibyte so convert to a string first,
    // optimization: first char is requested and is a single byte UTF-8 char
    if (index == 0) {
      byte b = byteArray[0];
      if ((b & 0xFF & 0x80) == 0) {
        return (char) b;
      }
    }
    return toString().charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    // toString() first, because UTF-8 chars can be multibyte
    return toString().subSequence(start, end);
  }

  @Override
  public String toString() {
    return new String(byteArray, offset, length, StandardCharsets.UTF_8);
  }
}
