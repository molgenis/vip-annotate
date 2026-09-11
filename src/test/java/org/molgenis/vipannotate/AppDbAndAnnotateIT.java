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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppDbAndAnnotateIT {
  private static final String EXPECTED_VCF_OUTPUT =
"""
##fileformat=VCFv4.5
##reference=file:///references/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna
##contig=<ID=chr1,length=248956422>
##INFO=<ID=my,NUMBER=A,TYPE=String,DESCRIPTION="my annotation formatted as 'my_enum'",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr1	1	.	G	A	.	.	my=B|P|-128|96|0|254|-32768|32766|0|65534|-2147483648|2147483646|-2.345|2.345|-23.456|23.456
chr1	2	.	G	A	.	.	my=LB|LP|-64|32|32|128|-16384|16384|8192|32768|-1073741824|1073741824|-1.234|1.234|-12.345|12.345
chr1	3	.	G	A	.	.	my=VUS||0||64||0||16384||0||0||0|
chr1	4	.	G	A	.	.	my=LP|LB|64|-32|128|32|16384|-16384|32768|8192|1073741824|-1073741824|1.234|-1.234|12.345|-12.345
chr1	5	.	G	A	.	.	my=P|B|127|-96|255|0|32767|-32768|65535|0|2147483647|-2147483648|2.345|-2.345|23.456|-23.456
              """;

  private Path dbDir;

  @BeforeEach
  public void beforeEach() throws IOException {
    dbDir = Files.createTempDirectory("vip-annotate-db");
  }

  @AfterEach
  public void afterEach() throws IOException {
    try (Stream<Path> paths = Files.walk(dbDir)) {
      paths
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.delete(path);
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                }
              });
    }
  }

  @Test
  public void createDbsAndAnnotate() {
    createDbs();
    String vcf = annotateVcf("annotate/all_types/input_all_types.vcf");

    // one of the goals of vip-annotate is compact annotation archives, so check size
    // update thresholds in case index got smaller
    assertAll(
        () -> assertEquals(69936L, Files.size(dbDir.resolve("all_types_sequence_variant_tsv.vdb"))),
        () -> assertEquals(EXPECTED_VCF_OUTPUT, vcf));
  }

  private void createDbs() {
    List<String> recipeList = List.of("all_types_sequence_variant_tsv.json");
    recipeList.forEach(
        recipeFilename ->
            App.main(
                new String[] {
                  "--debug",
                  "database-build",
                  "--recipe",
                  getResource("db/all_types/%s".formatted(recipeFilename)).toString(),
                  "--output-dir",
                  dbDir.toString()
                }));
  }

  private String annotateVcf(String vcfResourceName) {
    Path inputVcfFile = getResource(vcfResourceName);

    String[] args = {
      "annotate",
      "--annotations",
      dbDir.toString(),
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
