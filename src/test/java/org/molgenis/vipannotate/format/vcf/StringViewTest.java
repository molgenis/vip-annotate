package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class StringViewTest {
  private StringView stringView;

  @BeforeEach
  void setUp() {
    stringView = new StringView("12345", 1, 4);
  }

  @Test
  void length() {
    assertEquals(3, stringView.length());
  }

  @Test
  void charAt() {
    assertAll(
        () -> assertEquals('2', stringView.charAt(0)),
        () -> assertEquals('3', stringView.charAt(1)),
        () -> assertEquals('4', stringView.charAt(2)));
  }

  @Test
  void indexOf() {
    assertAll(
        () -> assertEquals(-1, stringView.indexOf('1')),
        () -> assertEquals(0, stringView.indexOf('2')),
        () -> assertEquals(1, stringView.indexOf('3')),
        () -> assertEquals(2, stringView.indexOf('4')),
        () -> assertEquals(-1, stringView.indexOf('5')));
  }

  @Test
  void indexOfFrom() {
    assertAll(
        () -> assertEquals(-1, stringView.indexOf('2', 1)),
        () -> assertEquals(1, stringView.indexOf('3', 1)));
  }

  @Test
  void indexOfFromTo() {
    assertAll(
        () -> assertEquals(-1, stringView.indexOf('2', 1, 3)),
        () -> assertEquals(1, stringView.indexOf('3', 1, 3)),
        () -> assertEquals(-1, stringView.indexOf('4', 1, 2)));
  }

  @Test
  void indexOfStatic() {
    assertAll(
        () -> assertEquals(1, StringView.indexOf("12345", '2')),
        () -> assertEquals(-1, StringView.indexOf("12345", '6')));
  }

  @Test
  void indexOfStaticFrom() {
    assertAll(
        () -> assertEquals(1, StringView.indexOf("12345", '2', 1)),
        () -> assertEquals(-1, StringView.indexOf("12345", '2', 3)));
  }

  @Test
  void indexOfStaticFromTo() {
    assertAll(
        () -> assertEquals(1, StringView.indexOf("12345", '2', 1, 3)),
        () -> assertEquals(-1, StringView.indexOf("12345", '2', 2, 3)));
  }

  @Test
  void subSequenceFrom() {
    assertEquals("34", stringView.subSequence(1).toString());
  }

  @Test
  void subSequenceFromTo() {
    assertEquals("3", stringView.subSequence(1, 2).toString());
  }

  @Test
  void testToString() {
    assertEquals("234", stringView.toString());
  }
}
