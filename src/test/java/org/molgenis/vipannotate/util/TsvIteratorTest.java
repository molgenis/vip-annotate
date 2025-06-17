package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class TsvIteratorTest {
  @Test
  void newWithNullIterator() {
    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> new TsvIterator(null));
  }

  @Test
  void hasNextNextHeaderOnly() throws IOException {
    String str = "#my_header";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void hasNextNextNewlineOnly() throws IOException {
    String str = "\n";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void hasNextNextDataOnly() throws IOException {
    String str = "a\tb\tc\nd\te\tf\n";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertTrue(tsvIterator.hasNext());
      assertArrayEquals(new String[] {"a", "b", "c"}, tsvIterator.next());
      assertArrayEquals(new String[] {"d", "e", "f"}, tsvIterator.next());
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void hasNextNextLastDataLineWithoutNewline() throws IOException {
    String str = "a\tb\tc\nd\te\tf";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertTrue(tsvIterator.hasNext());
      assertArrayEquals(new String[] {"a", "b", "c"}, tsvIterator.next());
      assertArrayEquals(new String[] {"d", "e", "f"}, tsvIterator.next());
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void hasNextNextMultipleLines() throws IOException {
    String str = "#my_header\na\tb\tc\n#my_other_header\nd\te\tf\n\ng\th\ti\n";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertTrue(tsvIterator.hasNext());
      assertArrayEquals(new String[] {"a", "b", "c"}, tsvIterator.next());
      assertArrayEquals(new String[] {"d", "e", "f"}, tsvIterator.next());
      assertArrayEquals(new String[] {"g", "h", "i"}, tsvIterator.next());
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void hasNextNextEmpty() throws IOException {
    String str = "";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertFalse(tsvIterator.hasNext());
    }
  }

  @Test
  void nextNoSuchElementException() throws IOException {
    String str = "";
    try (BufferedReader bufferedReader = new BufferedReader(new StringReader(str))) {
      TsvIterator tsvIterator = new TsvIterator(bufferedReader);
      assertThrows(NoSuchElementException.class, tsvIterator::next);
    }
  }
}
