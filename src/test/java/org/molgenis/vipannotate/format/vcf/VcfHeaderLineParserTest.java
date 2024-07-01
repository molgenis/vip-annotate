package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfHeaderLineParserTest {
  private VcfHeaderLineParser vcfHeaderLineParser;

  @BeforeEach
  void setUp() {
    vcfHeaderLineParser = new VcfHeaderLineParser();
  }

  @Test
  void parseLine() {
    String line = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO";
    assertEquals(new VcfHeaderLine(line), vcfHeaderLineParser.parseLine(line));
  }

  @Test
  void parseLineSamples() {
    String line = "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE0";
    assertEquals(new VcfHeaderLine(line), vcfHeaderLineParser.parseLine(line));
  }

  @Test
  void parseLineInvalid() {
    String line = "x";
    assertThrows(VcfParserException.class, () -> vcfHeaderLineParser.parseLine(line));
  }
}
