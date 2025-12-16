package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vipannotate.util.BufferedLineReader;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
@ExtendWith(MockitoExtension.class)
class VcfHeaderParserTest {
  @Mock private VcfMetaInfoLineParser vcfMetaInfoLineParser;
  @Mock private VcfHeaderLineParser vcfHeaderLineParser;
  private VcfHeaderParser vcfHeaderParser;

  @BeforeEach
  void setUp() {
    vcfHeaderParser = new VcfHeaderParser(vcfMetaInfoLineParser, vcfHeaderLineParser);
  }

  @Test
  void parse() {
    String vcfHeader =
        """
        ##key0
        ##key1=value1
        #CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO
        """;
    VcfHeaderLine vcfHeaderLine = mock(VcfHeaderLine.class);
    VcfMetaInfo.Line vcfMetaInfoLine0 = mock(VcfMetaInfo.Line.class);
    VcfMetaInfo.Line vcfMetaInfoLine1 = mock(VcfMetaInfo.Line.class);
    doReturn(vcfMetaInfoLine0).when(vcfMetaInfoLineParser).parseLine("##key0");
    doReturn(vcfMetaInfoLine1).when(vcfMetaInfoLineParser).parseLine("##key1=value1");
    when(vcfHeaderLineParser.parseLine("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO"))
        .thenReturn(vcfHeaderLine);
    try (BufferedLineReader reader = new BufferedLineReader(new StringReader(vcfHeader))) {
      assertEquals(
          new VcfHeader(
              new VcfMetaInfo(List.of(vcfMetaInfoLine0, vcfMetaInfoLine1)), vcfHeaderLine),
          vcfHeaderParser.parse(reader));
    }
  }

  private static Stream<Arguments> parseInvalidProvider() {
    return Stream.of(Arguments.of("##x=y\n##y=z\n"), Arguments.of("##x=y\nchr1\t123\n"));
  }

  @ParameterizedTest
  @MethodSource("parseInvalidProvider")
  void parseInvalid(String invalidVcfHeader) {
    try (BufferedLineReader reader = new BufferedLineReader(new StringReader(invalidVcfHeader))) {
      assertThrows(VcfParserException.class, () -> vcfHeaderParser.parse(reader));
    }
  }
}
