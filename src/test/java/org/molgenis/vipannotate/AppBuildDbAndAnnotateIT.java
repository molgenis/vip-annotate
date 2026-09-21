package org.molgenis.vipannotate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AppBuildDbAndAnnotateIT {
  @ParameterizedTest(name = "{0}")
  @MethodSource("testCases")
  void buildDbAndAnnotate(
      String inputFilename,
      String recipeFilename,
      String vcfFilename,
      String expectedVcfFilename,
      long expectedDbSize,
      @TempDir Path tmpDir) {
    long dbSize = buildDb(tmpDir, inputFilename, recipeFilename);
    String vcf = annotateVcf(tmpDir, vcfFilename);

    // one of the goals of vip-annotate is compact annotation archives, so check size
    // update thresholds in case index got smaller
    assertAll(
        () -> assertEquals(Files.readString(getResource(expectedVcfFilename), UTF_8), vcf),
        () -> assertEquals(expectedDbSize, dbSize));
  }

  static Stream<Arguments> testCases() {
    return Stream.of(
        Arguments.of(
            "seq_var_all_types.tsv",
            "seq_var_all_types.tsv.json",
            "seq_var_all_types.vcf",
            "seq_var_all_types.annotated.vcf",
            69937L),
        Arguments.of(
            "pos_encoding.tsv",
            "pos_encoding.tsv.json",
            "pos_encoding.vcf",
            "pos_encoding.annotated.vcf",
            20668L));
  }

  private long buildDb(Path dbDir, String inputFilename, String recipeFilename) {
    App.main(
        new String[] {
          "database-build",
          "--input",
          getResource(inputFilename).toString(),
          "--recipe",
          getResource(recipeFilename).toString(),
          "--output-dir",
          dbDir.toString()
        });
    String vdbFilename = recipeFilename.replaceFirst("\\.json$", ".vdb");

    try {
      return Files.size(dbDir.resolve(vdbFilename));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String annotateVcf(Path tmpDir, String vcfResourceName) {
    Path inputVcfFile = getResource(vcfResourceName);

    String[] args = {
      "annotate",
      "--annotations",
      tmpDir.toString(),
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
}
