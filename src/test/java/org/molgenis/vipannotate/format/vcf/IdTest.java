package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class IdTest {
  private static Stream<Arguments> getIdentifierArgsProvider() {
    return Stream.of(
        Arguments.of(".", List.of()),
        Arguments.of("id0", List.of("id0")),
        Arguments.of("id0;id1", List.of("id0", "id1")),
        Arguments.of("id0;id1;id2", List.of("id0", "id1", "id2")),
        Arguments.of("x", List.of("x")));
  }

  @ParameterizedTest
  @MethodSource("getIdentifierArgsProvider")
  void getIdentifiers(String fieldRaw, List<String> identifiersList) {
    assertEquals(
        identifiersList,
        Id.wrap(fieldRaw).getIdentifiers().stream().map(CharSequence::toString).toList());
  }

  @Test
  void getIdentifiersAfterReset() {
    Id id = Id.wrap("id0;id1");
    id.reset("id2;id3");
    assertEquals(
        List.of("id2", "id3"), id.getIdentifiers().stream().map(CharSequence::toString).toList());
  }

  @Test
  void getIdentifiersAfterResetFieldParsed() {
    Id id = Id.wrap("id0;id1");
    List<String> beforeIdentifiers =
        id.getIdentifiers().stream().map(CharSequence::toString).toList();
    id.reset("id2;id3");
    List<String> afterIdentifiers =
        id.getIdentifiers().stream().map(CharSequence::toString).toList();
    assertAll(
        () -> assertEquals(List.of("id0", "id1"), beforeIdentifiers),
        () -> assertEquals(List.of("id2", "id3"), afterIdentifiers));
  }

  private static Stream<Arguments> toStringArgsProvider() {
    return Stream.of(Arguments.of("."), Arguments.of("id0"));
  }

  @ParameterizedTest
  @MethodSource("toStringArgsProvider")
  void testToString(String fieldRaw) {
    assertEquals("ID=" + fieldRaw, Id.wrap(fieldRaw).toString());
  }
}
