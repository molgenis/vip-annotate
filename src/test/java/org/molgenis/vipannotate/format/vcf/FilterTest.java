package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FilterTest {
  private static Stream<Arguments> getCodesArgsProvider() {
    return Stream.of(
        Arguments.of(".", List.of()),
        Arguments.of("PASS", List.of("PASS")),
        Arguments.of("c0;c1", List.of("c0", "c1")),
        Arguments.of("c0;c1;c2", List.of("c0", "c1", "c2")),
        Arguments.of("c", List.of("c")));
  }

  @ParameterizedTest
  @MethodSource("getCodesArgsProvider")
  void getCodes(String fieldRaw, List<String> identifiersList) {
    assertEquals(
        identifiersList,
        Filter.wrap(fieldRaw).getCodes().stream().map(CharSequence::toString).toList());
  }

  @Test
  void getCodesAfterReset() {
    Filter filter = Filter.wrap("c0;c1");
    filter.reset("c2;c3");
    assertEquals(
        List.of("c2", "c3"), filter.getCodes().stream().map(CharSequence::toString).toList());
  }

  @Test
  void getCodesAfterResetFieldParsed() {
    Filter filter = Filter.wrap("c0;c1");
    List<String> beforeIdentifiers =
        filter.getCodes().stream().map(CharSequence::toString).toList();
    filter.reset("c2;c3");
    List<String> afterIdentifiers = filter.getCodes().stream().map(CharSequence::toString).toList();
    assertAll(
        () -> assertEquals(List.of("c0", "c1"), beforeIdentifiers),
        () -> assertEquals(List.of("c2", "c3"), afterIdentifiers));
  }

  @Test
  void isPassTrue() {
    assertTrue(Filter.wrap("PASS").isPass());
  }

  @Test
  void isPassTrueAfterFieldParsed() {
    Filter filter = Filter.wrap("PASS");
    filter.getCodes(); // triggers parsing
    assertTrue(filter.isPass());
  }

  @Test
  void isPassFalse() {
    assertFalse(Filter.wrap("c0").isPass());
  }

  @Test
  void isPassFalseAfterFieldParsed() {
    Filter filter = Filter.wrap("c0");
    filter.getCodes(); // triggers parsing
    assertFalse(filter.isPass());
  }

  @Test
  void testToString() {
    assertEquals("FILTER=PASS", Filter.wrap("PASS").toString());
  }
}
