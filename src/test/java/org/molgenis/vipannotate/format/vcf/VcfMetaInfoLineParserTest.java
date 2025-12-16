package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfMetaInfoLineParserTest {
  private VcfMetaInfoLineParser vcfMetaInfoLineParser;

  @BeforeEach
  void setUp() {
    vcfMetaInfoLineParser = new VcfMetaInfoLineParser();
  }

  private static Stream<Arguments> parseLineUnstructuredProvider() {
    return Stream.of(
        Arguments.of("##fileformat=VCFv4.5", "fileformat"),
        Arguments.of("##reference=file:///my.fna", "reference"));
  }

  @ParameterizedTest
  @MethodSource("parseLineUnstructuredProvider")
  void parseLineUnstructured(String line, String expectedKey) {
    VcfMetaInfo.Line parsedLine = vcfMetaInfoLineParser.parseLine(line);
    assertAll(
        () -> assertEquals(expectedKey, parsedLine.key()),
        () -> assertEquals(line, parsedLine.line()));
  }

  private static Stream<Arguments> parseLineUnstructuredInvalidProvider() {
    return Stream.of(Arguments.of("##key=<missing closing bracket"), Arguments.of("##key="));
  }

  @ParameterizedTest
  @MethodSource("parseLineUnstructuredInvalidProvider")
  void parseLineUnstructuredInvalid(String line) {
    assertThrows(VcfMetaInfoParserException.class, () -> vcfMetaInfoLineParser.parseLine(line));
  }

  private static Stream<Arguments> parseLineStructuredProvider() {
    return Stream.of(
        Arguments.of("##contig=<ID=chr2,length=242193529>", "contig"),
        Arguments.of("##ALT=<ID=DEL,Description=\"Deletion\">", "ALT"),
        Arguments.of(
            "##INFO=<ID=SVLEN,Number=A,Type=Integer,Description=\"Length of structural variant\">",
            "INFO"),
        Arguments.of(
            "##INFO=<ID=FATHMM_MKL,NUMBER=A,TYPE=String,DESCRIPTION=\"FATHMM-MKL score\",SOURCE=\"vip-annotate\",VERSION=\"0.0.0-dev\">",
            "INFO"));
  }

  @ParameterizedTest
  @MethodSource("parseLineStructuredProvider")
  void parseLineStructured(String line, String expectedKey) {
    VcfMetaInfo.Line parsedLine = vcfMetaInfoLineParser.parseLine(line);
    assertAll(
        () -> assertEquals(expectedKey, parsedLine.key()),
        () -> assertEquals(line, parsedLine.line()));
  }

  private static Stream<Arguments> parseLineStructuredInvalidProvider() {
    return Stream.of(Arguments.of("##key=<notID=chr2>"), Arguments.of("##key=<ID=>"));
  }

  @ParameterizedTest
  @MethodSource("parseLineStructuredInvalidProvider")
  void parseLineStructuredInvalid(String line) {
    assertThrows(VcfMetaInfoParserException.class, () -> vcfMetaInfoLineParser.parseLine(line));
  }

  private static Stream<Arguments> parseLineInvalidProvider() {
    return Stream.of(
        Arguments.of("x"),
        Arguments.of("##key_without_value"),
        Arguments.of("##=empty_value"),
        Arguments.of("#CHROM"));
  }

  @ParameterizedTest
  @MethodSource("parseLineInvalidProvider")
  void parseLineInvalid(String line) {
    assertThrows(VcfMetaInfoParserException.class, () -> vcfMetaInfoLineParser.parseLine(line));
  }
}
