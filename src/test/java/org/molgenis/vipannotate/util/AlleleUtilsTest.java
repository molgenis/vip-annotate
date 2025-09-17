package org.molgenis.vipannotate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("DataFlowIssue")
class AlleleUtilsTest {
  private static Stream<Arguments> altActgProvider() {
    return Stream.of(
        Arguments.of("A"),
        Arguments.of("C"),
        Arguments.of("T"),
        Arguments.of("G"),
        Arguments.of("ACTG"));
  }

  private static Stream<Arguments> altOtherProvider() {
    return Stream.of(
        Arguments.of(""),
        Arguments.of("N"),
        Arguments.of("<CNV:TR>"),
        Arguments.of("<*>"),
        Arguments.of("."),
        Arguments.of("T[chrA:5["));
  }

  @ParameterizedTest
  @MethodSource("altActgProvider")
  void isActg(String alt) {
    assertTrue(AlleleUtils.isActg(alt));
  }

  @ParameterizedTest
  @MethodSource("altOtherProvider")
  void isActgFalse(String alt) {
    assertFalse(AlleleUtils.isActg(alt));
  }
}
