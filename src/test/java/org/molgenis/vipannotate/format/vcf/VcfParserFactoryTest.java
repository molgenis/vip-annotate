package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.util.Input;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfParserFactoryTest {
  private static final String VCF_STR =
      """
            ##fileformat=VCFv4.5
            #CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT
            """;
  private static final String VCF_STR_SAMPLES =
"""
##fileformat=VCFv4.5
#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSAMPLE0\tSAMPLE1
""";

  private static Stream<Arguments> createInputFileCompressedProvider() {
    return Stream.of(Arguments.of(".gz"), Arguments.of(".bgz"));
  }

  @ParameterizedTest
  @MethodSource("createInputFileCompressedProvider")
  void createInputFileCompressed(String suffix) throws IOException {
    Path vcfPath = Files.createTempFile("vip-annotate", suffix);
    try {
      try (GZIPOutputStream gzipOutputStream =
          new GZIPOutputStream(Files.newOutputStream(vcfPath))) {
        gzipOutputStream.write(VCF_STR.getBytes(StandardCharsets.UTF_8));
      }
      try (VcfParser vcfParser = VcfParserFactory.create(new Input(vcfPath))) {
        assertEquals(0, vcfParser.getHeader().getNrSamples());
      }
    } finally {
      Files.delete(vcfPath);
    }
  }

  @Test
  void createInputFileUncompressed() throws IOException {
    Path vcfPath = Files.createTempFile("vip-annotate", "vcf");
    try {
      Files.writeString(vcfPath, VCF_STR_SAMPLES, StandardCharsets.UTF_8);
      try (VcfParser vcfParser = VcfParserFactory.create(new Input(vcfPath))) {
        assertEquals(2, vcfParser.getHeader().getNrSamples());
      }
    } finally {
      Files.delete(vcfPath);
    }
  }

  @Test
  void createInputStdInCompressed() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
      gzipOutputStream.write(VCF_STR_SAMPLES.getBytes(StandardCharsets.UTF_8));
    }
    try (ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
      System.setIn(byteArrayInputStream);
      try (VcfParser vcfParser = VcfParserFactory.create(new Input())) {
        assertEquals(2, vcfParser.getHeader().getNrSamples());
      }
    }
  }

  @Test
  void createInputStdInUncompressed() {
    System.setIn(new ByteArrayInputStream(VCF_STR.getBytes(StandardCharsets.UTF_8)));
    try (VcfParser vcfParser = VcfParserFactory.create(new Input())) {
      assertEquals(0, vcfParser.getHeader().getNrSamples());
    }
  }
}
