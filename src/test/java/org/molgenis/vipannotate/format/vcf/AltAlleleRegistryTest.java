package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AltAlleleRegistryTest {
  private static Stream<Arguments> getPrecomputedProvider() {
    return Stream.of(
        Arguments.of("A"),
        Arguments.of("C"),
        Arguments.of("G"),
        Arguments.of("T"),
        Arguments.of("AA"),
        Arguments.of("AC"),
        Arguments.of("AG"),
        Arguments.of("AT"),
        Arguments.of("CA"),
        Arguments.of("CC"),
        Arguments.of("CG"),
        Arguments.of("CT"),
        Arguments.of("GA"),
        Arguments.of("GC"),
        Arguments.of("GG"),
        Arguments.of("GT"),
        Arguments.of("TA"),
        Arguments.of("TC"),
        Arguments.of("TG"),
        Arguments.of("TT"));
  }

  @ParameterizedTest
  @MethodSource("getPrecomputedProvider")
  void getPrecomputed(CharSequence charSequence) {
    assertEquals(new AltAllele(charSequence), AltAlleleRegistry.INSTANCE.get(charSequence));
  }

  private static Stream<Arguments> getNewProvider() {
    return Stream.of(
        Arguments.of("N"),
        Arguments.of("AN"),
        Arguments.of("CN"),
        Arguments.of("GN"),
        Arguments.of("TN"),
        Arguments.of("NN"),
        Arguments.of("AAA"));
  }

  @ParameterizedTest
  @MethodSource("getNewProvider")
  void getNew(CharSequence charSequence) {
    assertEquals(new AltAllele(charSequence), AltAlleleRegistry.INSTANCE.get(charSequence));
  }
}
