package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AltAlleleTest {
  @Test
  void getAlleles() {
    AltAllele allele = new AltAllele("ACTG");
    assertEquals("ACTG", allele.get().toString());
  }

  @Test
  void typeMissing() {
    AltAllele allele = new AltAllele(".");
    assertEquals(AltAlleleType.MISSING, allele.getType());
  }

  @Test
  void typeMissingOverlappingDeletion() {
    AltAllele allele = new AltAllele("*");
    assertEquals(AltAlleleType.MISSING_OVERLAPPING_DELETION, allele.getType());
  }

  private static Stream<Arguments> basesArgsProvider() {
    return Stream.of(
        Arguments.of("A"),
        Arguments.of("AC"),
        Arguments.of("ACG"),
        Arguments.of("ACGT"),
        Arguments.of("a"),
        Arguments.of("acgt"));
  }

  @ParameterizedTest
  @MethodSource("basesArgsProvider")
  void typeBases(String bases) {
    AltAllele allele = new AltAllele(bases);
    assertEquals(AltAlleleType.BASES, allele.getType());
  }

  @Test
  void typeUnspecified() {
    AltAllele allele = new AltAllele("<*>");
    assertEquals(AltAlleleType.UNSPECIFIED, allele.getType());
  }

  @Test
  void typeSymbolic() {
    AltAllele allele = new AltAllele("<DEL>");
    assertEquals(AltAlleleType.SYMBOLIC, allele.getType());
  }

  @Test
  void typeSymbolicSingleChar() {
    AltAllele allele = new AltAllele("<D>");
    assertEquals(AltAlleleType.SYMBOLIC, allele.getType());
  }

  @Test
  void typeBreakendReplacementStartingWithBracket() {
    AltAllele allele = new AltAllele("]13:12345]A");
    assertEquals(AltAlleleType.BREAKEND_REPLACEMENT, allele.getType());
  }

  @Test
  void typeBreakendReplacementEndingWithBracket() {
    AltAllele allele = new AltAllele("A[13:12345[");
    assertEquals(AltAlleleType.BREAKEND_REPLACEMENT, allele.getType());
  }

  @Test
  void typeSingleBreakendStartingWithDot() {
    AltAllele allele = new AltAllele(".A");
    assertEquals(AltAlleleType.SINGLE_BREAKEND, allele.getType());
  }

  @Test
  void typeSingleBreakendEndingWithDot() {
    AltAllele allele = new AltAllele("T.");
    assertEquals(AltAlleleType.SINGLE_BREAKEND, allele.getType());
  }

  @SuppressWarnings({"SimplifiableAssertion", "UnnecessaryStringBuilder"})
  @Test
  void equalsTrue() {
    AltAllele thisAllele = new AltAllele("ACTG");
    AltAllele thatAllele = new AltAllele(new StringBuilder("ACTG"));
    assertAll(
        () -> assertTrue(thisAllele.equals(thatAllele)),
        () -> assertTrue(thatAllele.equals(thisAllele)));
  }

  @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself"})
  @Test
  void equalsSameTrue() {
    AltAllele thisAllele = new AltAllele("ACTG");
    assertTrue(thisAllele.equals(thisAllele));
  }

  @SuppressWarnings("SimplifiableAssertion")
  @Test
  void equalsFalse() {
    AltAllele thisAllele = new AltAllele("ACTG");
    @SuppressWarnings("UnnecessaryStringBuilder")
    AltAllele thatAllele = new AltAllele(new StringBuilder("ACT"));
    assertFalse(thisAllele.equals(thatAllele));
  }

  @SuppressWarnings({
    "SimplifiableAssertion",
    "EqualsBetweenInconvertibleTypes",
    "EqualsOnSuspiciousObject"
  })
  @Test
  void equalsFalseDifferentTypes() {
    AltAllele thisAllele = new AltAllele("ACTG");
    @SuppressWarnings("UnnecessaryStringBuilder")
    StringBuilder thatStringBuilder = new StringBuilder("ACTG");
    assertFalse(thisAllele.equals(thatStringBuilder));
  }

  @Test
  void hashcodeEquals() {
    AltAllele thisAllele = new AltAllele("ACTG");
    @SuppressWarnings("UnnecessaryStringBuilder")
    AltAllele thatAllele = new AltAllele(new StringBuilder("ACTG"));
    assertEquals(thisAllele.hashCode(), thatAllele.hashCode());
  }

  @Test
  void hashcodeNotEquals() {
    AltAllele thisAllele = new AltAllele("ACTG");
    AltAllele thatAllele = new AltAllele("ACT");
    assertNotEquals(thisAllele.hashCode(), thatAllele.hashCode());
  }

  @Test
  void testToString() {
    assertEquals("ACTG", new AltAllele("ACTG").toString());
  }
}
