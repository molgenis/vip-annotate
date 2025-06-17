package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CharArrayBufferTest {
  @Test
  void newInvalidCapacity() {
    assertThrows(IllegalArgumentException.class, () -> new CharArrayBuffer(-1));
  }

  @Test
  void appendWithinCapacity() {
    CharArrayBuffer charArrayBuffer = new CharArrayBuffer(10);
    charArrayBuffer.append('a');
    charArrayBuffer.append("bc");
    charArrayBuffer.append('d');
    assertEquals("abcd", charArrayBuffer.toString());
  }

  @Test
  void appendOutsideCapacity() {
    CharArrayBuffer charArrayBuffer = new CharArrayBuffer(2);
    charArrayBuffer.append('a');
    charArrayBuffer.append("bc");
    charArrayBuffer.append('d');
    assertEquals("abcd", charArrayBuffer.toString());
  }

  @Test
  void clear() {
    CharArrayBuffer charArrayBuffer = new CharArrayBuffer(10);
    charArrayBuffer.append("abc");
    charArrayBuffer.clear();
    assertEquals("", charArrayBuffer.toString());
  }
}
