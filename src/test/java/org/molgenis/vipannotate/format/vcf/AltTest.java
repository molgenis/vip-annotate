package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class AltTest {
  private static Stream<Arguments> getAllelesArgsProvider() {
    return Stream.of(
        Arguments.of(".", List.of()),
        Arguments.of("A", List.of(new AltAllele("A"))),
        Arguments.of("A,C", List.of(new AltAllele("A"), new AltAllele("C"))),
        Arguments.of("A,C,G", List.of(new AltAllele("A"), new AltAllele("C"), new AltAllele("G"))));
  }

  @ParameterizedTest
  @MethodSource("getAllelesArgsProvider")
  void getAlleles(String fieldRaw, List<AltAllele> altAlleleList) {
    assertEquals(altAlleleList, Alt.wrap(fieldRaw).getAlleles());
  }

  @Test
  void getAllelesAfterReset() {
    Alt alt = Alt.wrap("A");
    alt.reset("C");
    assertEquals(List.of(new AltAllele("C")), alt.getAlleles());
  }

  @Test
  void getAllelesAfterResetFieldParsed() {
    Alt alt = Alt.wrap("A");
    alt.getAlleles();
    alt.reset("C");
    assertEquals(List.of(new AltAllele("C")), alt.getAlleles());
  }

  private static Stream<Arguments> getFirstAlleleProvider() {
    return Stream.of(
        Arguments.of("A", "A"),
        Arguments.of("ACTG", "ACTG"),
        Arguments.of("A,C", "A"),
        Arguments.of("ACTG,CT", "ACTG"));
  }

  @ParameterizedTest
  @MethodSource("getFirstAlleleProvider")
  void getFirstAllele(String altStr, String firstAlt) {
    assertEquals(firstAlt, Alt.wrap(altStr).getFirstAllele().toString());
  }

  @Test
  void getFirstAlleleNoAlleles() {
    assertThrows(IllegalArgumentException.class, () -> Alt.wrap(".").getFirstAllele());
  }

  @Test
  void testToString() {
    assertEquals("ALT=A,C", Alt.wrap("A,C").toString());
  }
}
