package org.molgenis.vipannotate.annotation.gnomad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.format.fasta.FastaIndex;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class GnomAdParserTest {
  @Mock FastaIndex fastaIndex;
  private GnomAdParser gnomAdParser;

  @BeforeEach
  void setUp() {
    gnomAdParser = new GnomAdParser(fastaIndex);
  }

  @Test
  void parse() {
    when(fastaIndex.containsReferenceSequence("chr1")).thenReturn(true);
    assertEquals(
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            0.1d,
            0.2d,
            0.3d,
            1.1d,
            1.2d,
            1.3d,
            2.1d,
            2.2d,
            2.3d,
            12,
            23,
            34,
            EnumSet.of(GnomAdTsvRecord.Filter.AC0),
            EnumSet.of(GnomAdTsvRecord.Filter.AS_VQSR, GnomAdTsvRecord.Filter.INBREEDING_COEFF),
            false,
            true,
            3.1d,
            3.2d,
            3.3d),
        gnomAdParser.parse(
            new String[] {
              "chr1",
              "123",
              "A",
              "C",
              "0.1",
              "0.2",
              "0.3",
              "1.1",
              "1.2",
              "1.3",
              "2.1",
              "2.2",
              "2.3",
              "12",
              "23",
              "34",
              "AC0",
              "AS_VQSR,InbreedingCoeff",
              "",
              "1",
              "3.1",
              "3.2",
              "3.3"
            }));
  }

  @Test
  void parseMissingValues() {
    when(fastaIndex.containsReferenceSequence("chr1")).thenReturn(true);
    assertEquals(
        new GnomAdTsvRecord(
            "chr1",
            123,
            "A",
            "C",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            EnumSet.noneOf(GnomAdTsvRecord.Filter.class),
            EnumSet.noneOf(GnomAdTsvRecord.Filter.class),
            true,
            false,
            null,
            null,
            null),
        gnomAdParser.parse(
            new String[] {
              "chr1", "123", "A", "C", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "1",
              "", "", "", ""
            }));
  }

  @Test
  void parseUnknownChrom() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            gnomAdParser.parse(
                new String[] {
                  "chr1",
                  "123",
                  "A",
                  "C",
                  "0.1",
                  "0.2",
                  "0.3",
                  "1.1",
                  "1.2",
                  "1.3",
                  "2.1",
                  "2.2",
                  "2.3",
                  "12",
                  "23",
                  "34",
                  "AC0",
                  "AS_VQSR,InbreedingCoeff",
                  "",
                  "1",
                  "3.1",
                  "3.2",
                  "3.3"
                }));
  }

  @Test
  void parseInvalidNumberOfTokens() {
    assertThrows(IllegalArgumentException.class, () -> gnomAdParser.parse(new String[] {"chr1"}));
  }
}
