package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class ChromTest {
  private static Stream<Arguments> getIdentifierArgsProvider() {
    return Stream.of(
        Arguments.of("1", "1"),
        Arguments.of("chr1", "chr1"),
        Arguments.of("<1>", "1"),
        Arguments.of("<chr1>", "chr1"));
  }

  @ParameterizedTest
  @MethodSource("getIdentifierArgsProvider")
  void getIdentifier(String fieldRaw, String chromIdentifier) {
    assertEquals(chromIdentifier, Chrom.wrap(fieldRaw).getIdentifier().toString());
  }

  @Test
  void getIdentifierAfterReset() {
    Chrom chrom = Chrom.wrap("chr1");
    chrom.reset("chr2");
    assertEquals("chr2", chrom.getIdentifier().toString());
  }

  private static Stream<Arguments> getTypeArgsProvider() {
    return Stream.of(
        Arguments.of("1", ChromType.IDENTIFIER),
        Arguments.of("chr1", ChromType.IDENTIFIER),
        Arguments.of("<1>", ChromType.SYMBOLIC),
        Arguments.of("<chr1>", ChromType.SYMBOLIC));
  }

  @ParameterizedTest
  @MethodSource("getTypeArgsProvider")
  void getType(String fieldRaw, ChromType chromType) {
    assertEquals(chromType, Chrom.wrap(fieldRaw).getType());
  }

  private static Stream<Arguments> toStringArgsProvider() {
    return Stream.of(
        Arguments.of("1"), Arguments.of("chr1"), Arguments.of("<1>"), Arguments.of("<chr1>"));
  }

  @ParameterizedTest
  @MethodSource("toStringArgsProvider")
  void toString(String fieldRaw) {
    assertEquals("CHROM=" + fieldRaw, Chrom.wrap(fieldRaw).toString());
  }
}
