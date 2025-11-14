package org.molgenis.vipannotate.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SequenceVariantEncoderUtilsTest {
  // TODO add other encodePos test cases after resolving TODO in method
  @Test
  void encodePosZeroTelomere() {
    int telomerePos = 0;
    assertDoesNotThrow(() -> SequenceVariantEncoderUtils.encodePos(telomerePos));
  }

  @Test
  void encodeBaseCount() {
    assertEquals(3, SequenceVariantEncoderUtils.encodeBaseCount(4));
  }

  @Test
  void encodeBaseCountZeroInvalid() {
    assertThrows(
        IllegalArgumentException.class, () -> SequenceVariantEncoderUtils.encodeBaseCount(0));
  }

  private static Stream<Arguments> encodeActgBaseProvider() {
    return Stream.of(
        Arguments.of('A', 0),
        Arguments.of('C', 1),
        Arguments.of('G', 2),
        Arguments.of('T', 3),
        Arguments.of('a', 0),
        Arguments.of('c', 1),
        Arguments.of('g', 2),
        Arguments.of('t', 3));
  }

  @ParameterizedTest
  @MethodSource("encodeActgBaseProvider")
  void encodeActgBase(char base, int expectedEncoding) {
    assertEquals(expectedEncoding, SequenceVariantEncoderUtils.encodeActgBase(base));
  }

  @Test
  void encodeActgBaseInvalid() {
    assertThrows(
        IllegalArgumentException.class, () -> SequenceVariantEncoderUtils.encodeActgBase('N'));
  }

  private static Stream<Arguments> encodeActgnBaseProvider() {
    return Stream.of(
        Arguments.of('A', 0),
        Arguments.of('C', 1),
        Arguments.of('G', 2),
        Arguments.of('T', 3),
        Arguments.of('N', 4),
        Arguments.of('a', 0),
        Arguments.of('c', 1),
        Arguments.of('g', 2),
        Arguments.of('t', 3),
        Arguments.of('n', 4));
  }

  @ParameterizedTest
  @MethodSource("encodeActgnBaseProvider")
  void encodeActgnBase(char base, int expectedEncoding) {
    assertEquals(expectedEncoding, SequenceVariantEncoderUtils.encodeActgnBase(base));
  }

  @Test
  void encodeActgnBaseInvalid() {
    assertThrows(
        IllegalArgumentException.class, () -> SequenceVariantEncoderUtils.encodeActgBase('U'));
  }
}
