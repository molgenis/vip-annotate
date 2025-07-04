package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VcfTokenizerTest {
  private static Stream<Arguments> strProvider() {
    return Stream.of(
        Arguments.of("abc\tdef", List.of("abc", "def"), 1 << 16),
        Arguments.of("abc\tdef", List.of("abc", "def"), 4),
        Arguments.of("abc\tdef", List.of("abc", "def"), 5),
        Arguments.of("abc\tdef", List.of("abc", "def"), 6),
        Arguments.of("abc\tdef", List.of("abc", "def"), 7),
        Arguments.of("abc\tdef", List.of("abc", "def"), 8),
        Arguments.of("abc\ndef", List.of("abc", "\n", "def"), 1 << 16),
        Arguments.of("abc\tdef\n", List.of("abc", "def", "\n"), 1 << 16));
  }

  @ParameterizedTest
  @MethodSource("strProvider")
  void nextToken(String str, List<String> expectedTokens, int bufferSize) {
    List<String> actualTokens = new ArrayList<>();
    try (VcfTokenizer vcfTokenizer =
        new VcfTokenizer(
            new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), bufferSize)) {
      CharSequence charSequence;
      while ((charSequence = vcfTokenizer.nextToken()) != null) {
        actualTokens.add(charSequence.toString());
      }
    }
    assertEquals(expectedTokens, actualTokens);
  }

  @Test
  void parse() throws IOException {
    final int inputStreamReaderBufferSize = 32768;

    Path filePath =
        Path.of(
            "C:\\Users\\Dennis Hendriksen\\Dev\\_data\\vip-annotate\\rawdata\\spliceai_scores.masked.snv.hg38.vcf.gz");

    try (VcfTokenizer vcfTokenizer =
        new VcfTokenizer(
            new GZIPInputStream(
                new FileInputStream(filePath.toFile()), inputStreamReaderBufferSize))) {
      int nrTokens = 0;
      CharSequence token;
      while ((token = vcfTokenizer.nextToken()) != null) {
        String tokenStr = token.toString();
        if (!tokenStr.equals("\n")) System.out.println(tokenStr);
        nrTokens++;
      }
      System.out.println(nrTokens);
    }
  }
}
