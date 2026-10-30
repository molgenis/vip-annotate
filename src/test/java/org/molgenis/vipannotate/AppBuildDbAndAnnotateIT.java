package org.molgenis.vipannotate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AppBuildDbAndAnnotateIT {
  static Stream<Arguments> buildDbAndAnnotateProvider() {
    return Stream.of(
        of("seq_var_all_types", "tsv", 74092L, "9291bad8434ac94ce4dfff53cb1f35b7"),
        of("pos_encoding", "tsv", 16561L, "e581c4151366565a971ca595ce3e6de4"),
        of("score", "bed", 8328L, "25447e8ba34abec283e727acacaeb2dc"));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("buildDbAndAnnotateProvider")
  void buildDbAndAnnotate(
      String inputFilename,
      String inputDefFilename,
      String vcfFilename,
      String expectedVcfFilename,
      long expectedDbSize,
      String expectedMd5,
      @TempDir Path tmpDir) {
    Path outputPath = Paths.get(tmpDir.toString(), inputFilename + ".vdb");

    buildDb(inputDefFilename, inputFilename, outputPath);
    String vcf = annotateVcf(outputPath, vcfFilename);

    assertAll(
        () -> assertEquals(Files.readString(getResource(expectedVcfFilename), UTF_8), vcf),
        () -> assertEquals(expectedDbSize, Files.size(outputPath)), // unintentional db growth?
        () -> assertEquals(expectedMd5, md5(outputPath))); // unintentional db format change?
  }

  private void buildDb(String inputDefFilename, String inputFilename, Path outputPath) {
    App.main(
        new String[] {
          "database-build",
          "--definition",
          getResource(inputDefFilename).toString(),
          "--input",
          getResource(inputFilename).toString(),
          "--output",
          outputPath.toString()
        });
  }

  private String annotateVcf(Path inputPath, String vcfResourceName) {
    Path inputVcfFile = getResource(vcfResourceName);

    String[] args = {
      "annotate",
      "--annotations",
      inputPath.toString(),
      "--input",
      inputVcfFile.toString(),
      "--output",
      "-"
    };

    PrintStream originalOutputStream = System.out;
    String output;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (PrintStream outputStream = new PrintStream(byteArrayOutputStream, true, UTF_8)) {
      System.setOut(outputStream);
      try {
        App.main(args);
      } finally {
        System.setOut(originalOutputStream);
      }
      output = byteArrayOutputStream.toString(UTF_8);
    }
    return output;
  }

  private Path getResource(String name) {
    ClassLoader classLoader = getClass().getClassLoader();
    Path filePath;
    try {
      URL resourceUrl = classLoader.getResource(name);
      if (resourceUrl == null) {
        throw new IllegalArgumentException("Resource not found: %s".formatted(name));
      }
      filePath = Paths.get(resourceUrl.toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return filePath;
  }

  private static String md5(Path path) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    try (InputStream inputStream = Files.newInputStream(path)) {
      byte[] buffer = new byte[8192];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
        messageDigest.update(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return HexFormat.of().formatHex(messageDigest.digest());
  }

  private static Arguments of(
      String prefix, String suffix, long expectedDbSize, String expectedMd5) {
    return Arguments.of(
        "%s.%s".formatted(prefix, suffix),
        "%s.%s.json".formatted(prefix, suffix),
        "%s.vcf".formatted(prefix),
        "%s.annotated.vcf".formatted(prefix),
        expectedDbSize,
        expectedMd5);
  }
}
