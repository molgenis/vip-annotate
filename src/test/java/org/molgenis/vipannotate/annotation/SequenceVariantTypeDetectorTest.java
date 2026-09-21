package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.format.vcf.AltAllele;

class SequenceVariantTypeDetectorTest {
  @Test
  void determineTypeSnv() {
    assertEquals(
        SequenceVariantType.SNV, SequenceVariantTypeDetector.determineType(1, new AltAllele("A")));
  }

  @Test
  void determineTypeMnv() {
    assertEquals(
        SequenceVariantType.MNV, SequenceVariantTypeDetector.determineType(2, new AltAllele("AC")));
  }

  private static Stream<Arguments> insertionProvider() {
    return Stream.of(Arguments.of("AC"), Arguments.of("ACG"), Arguments.of("ACGT"));
  }

  @ParameterizedTest
  @MethodSource("insertionProvider")
  void determineTypeInsertion(String altAlleleFieldRaw) {
    assertEquals(
        SequenceVariantType.INSERTION,
        SequenceVariantTypeDetector.determineType(1, new AltAllele(altAlleleFieldRaw)));
  }

  @Test
  void determineTypeDeletion() {
    assertEquals(
        SequenceVariantType.DELETION,
        SequenceVariantTypeDetector.determineType(2, new AltAllele("A")));
  }

  @Test
  void determineTypeIndel() {
    assertEquals(
        SequenceVariantType.INDEL,
        SequenceVariantTypeDetector.determineType(3, new AltAllele("AC")));
  }

  private static Stream<Arguments> structuralProvider() {
    return Stream.of(
        Arguments.of("<CNV:TR>"),
        Arguments.of("T[chrA:5["),
        Arguments.of("]chrA:5]T"),
        Arguments.of(".T"),
        Arguments.of("T."));
  }

  @ParameterizedTest
  @MethodSource("structuralProvider")
  void determineTypeStructural(String altAlleleFieldRaw) {
    assertEquals(
        SequenceVariantType.STRUCTURAL,
        SequenceVariantTypeDetector.determineType(1, new AltAllele(altAlleleFieldRaw)));
  }

  private static Stream<Arguments> otherProvider() {
    return Stream.of(Arguments.of("."), Arguments.of("<*>"));
  }

  @ParameterizedTest
  @MethodSource("otherProvider")
  void determineTypeOther(String altAlleleFieldRaw) {
    assertEquals(
        SequenceVariantType.OTHER,
        SequenceVariantTypeDetector.determineType(1, new AltAllele(altAlleleFieldRaw)));
  }
}
