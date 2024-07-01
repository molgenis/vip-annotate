package org.molgenis.vipannotate.format.vcf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.vipannotate.util.Output;

@SuppressWarnings({"DataFlowIssue", "NullAway"})
class VcfWriterFactoryTest {
  private static Stream<Arguments> createOutputCompressedProvider() {
    return Stream.of(Arguments.of(".gz"), Arguments.of(".bgz"));
  }

  @ParameterizedTest
  @MethodSource("createOutputCompressedProvider")
  void createOutputCompressed(String suffix) throws IOException {
    Path vcfPath = Files.createTempFile("vip-annotate", suffix);
    try {
      try (VcfWriter vcfWriter = VcfWriterFactory.create(new Output(vcfPath))) {
        vcfWriter.writeHeader(
            new VcfHeader(new VcfMetaInfo(List.of()), new VcfHeaderLine("#CHROM")));
      }
      assertArrayEquals(
          new byte[] {(byte) 0x1F, (byte) 0x8B},
          Arrays.copyOfRange(Files.readAllBytes(vcfPath), 0, 2));

    } finally {
      Files.delete(vcfPath);
    }
  }

  @Test
  void createOutputUncompressed() throws IOException {
    Path vcfPath = Files.createTempFile("vip-annotate", ".vcf");
    try {
      try (VcfWriter vcfWriter = VcfWriterFactory.create(new Output(vcfPath))) {
        vcfWriter.writeHeader(
            new VcfHeader(new VcfMetaInfo(List.of()), new VcfHeaderLine("#CHROM")));
      }
      assertEquals("#CHROM\n", Files.readString(vcfPath, StandardCharsets.UTF_8));
    } finally {
      Files.delete(vcfPath);
    }
  }

  @Test
  void createOutputStream() {
    String line = "xyz";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (VcfWriter vcfWriter = VcfWriterFactory.create(outputStream)) {
      vcfWriter.writeHeader(new VcfHeader(new VcfMetaInfo(List.of()), new VcfHeaderLine(line)));
    }
    assertEquals(line + '\n', outputStream.toString(StandardCharsets.UTF_8));
  }

  @Test
  void createGzipOutputStream() {
    String line = "xyz";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (VcfWriter vcfWriter = VcfWriterFactory.createGzip(outputStream)) {
      vcfWriter.writeHeader(new VcfHeader(new VcfMetaInfo(List.of()), new VcfHeaderLine(line)));
    }
    assertArrayEquals(
        new byte[] {(byte) 0x1F, (byte) 0x8B},
        Arrays.copyOfRange(outputStream.toByteArray(), 0, 2));
  }

  private static Stream<Arguments> createGzipOutputStreamLevelProvider() {
    return Stream.of(
        Arguments.of(1),
        Arguments.of(2),
        Arguments.of(3),
        Arguments.of(4),
        Arguments.of(5),
        Arguments.of(6),
        Arguments.of(7),
        Arguments.of(8),
        Arguments.of(9));
  }

  @ParameterizedTest
  @MethodSource("createGzipOutputStreamLevelProvider")
  void createGzipOutputStreamLevel() {
    String line = "xyz";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (VcfWriter vcfWriter = VcfWriterFactory.createGzip(outputStream, 9)) {
      vcfWriter.writeHeader(new VcfHeader(new VcfMetaInfo(List.of()), new VcfHeaderLine(line)));
    }
    assertArrayEquals(
        new byte[] {(byte) 0x1F, (byte) 0x8B},
        Arrays.copyOfRange(outputStream.toByteArray(), 0, 2));
  }

  private static Stream<Arguments> createGzipOutputStreamLevelOobProvider() {
    return Stream.of(Arguments.of(-1), Arguments.of(10));
  }

  @ParameterizedTest
  @MethodSource("createGzipOutputStreamLevelOobProvider")
  void createGzipOutputStreamLevelOob(int level) {
    assertThrows(
        IllegalArgumentException.class,
        () -> VcfWriterFactory.createGzip(mock(OutputStream.class), level));
  }
}
