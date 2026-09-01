package org.molgenis.vipannotate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
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
              ##INFO=<ID=FATHMM_MKL,NUMBER=A,TYPE=Float,DESCRIPTION="FATHMM-MKL score",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
              ##INFO=<ID=gnomAD,NUMBER=A,TYPE=String,DESCRIPTION="gnomAD v4.1.0 annotation formatted as 'SRC|AF|FAF95|FAF99|HN|QC|COV'; SRC=source (E=exomes, G=genomes, T=total), AF=allele frequency, FAF95=filtering allele frequency (95% confidence), FAF99=filtering allele frequency (99% confidence), HN=number of homozygotes, QC=quality control filters that failed, COV=coverage (percent of individuals in gnomAD source)",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
              ##INFO=<ID=ncER,NUMBER=A,TYPE=Float,DESCRIPTION="ncER score",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
              ##INFO=<ID=phyloP,NUMBER=A,TYPE=Float,DESCRIPTION="phyloP score",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
              ##INFO=<ID=REMM,NUMBER=A,TYPE=Float,DESCRIPTION="REMM score",SOURCE="vip-annotate",VERSION="0.0.0-dev+db1.0.0">
              #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
              chr1	1048426	.	G	A	.	.	FATHMM_MKL=0.133;gnomAD=T|0|0|0|0||0.7699;ncER=95.642;phyloP=-0.288;REMM=0.024
              chr1	1048426	.	G	GT	.	.	ncER=95.642;phyloP=-0.288;REMM=0.024
              chr1	1048426	.	GT	G	.	.	ncER=95.898;phyloP=-0.288;REMM=0.043
              chr1	1048426	.	GTG	G	.	.	ncER=96.788;phyloP=-0.288;REMM=0.402
              chr1	1048426	.	GTGG	G	.	.	ncER=98.242;phyloP=-0.288;REMM=0.402
              chr1	1048426	.	GTGGG	G	.	.	ncER=98.242;phyloP=-0.288;REMM=0.402
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
    String vcf = annotateVcf("annotate/chr1_1048426-1048726/input_annotate.vcf");

    // one of the goals of vip-annotate is compact annotation archives, so check size
    // update thresholds in case index got smaller
    assertAll(
        () -> assertEquals(16723L, Files.size(dbDir.resolve("fathmm.vdb"))),
        () -> assertEquals(25068L, Files.size(dbDir.resolve("gnomad.vdb"))),
        () -> assertEquals(12451L, Files.size(dbDir.resolve("ncer.vdb"))),
        () -> assertEquals(12451L, Files.size(dbDir.resolve("phylop.vdb"))),
        () -> assertEquals(12448L, Files.size(dbDir.resolve("remm.vdb"))),
        () -> assertEquals(EXPECTED_VCF_OUTPUT, vcf));
  }

  private void createDbs() {
    List<String> recipeList =
        List.of("fathmm.json", "gnomad.json", "ncer.json", "phylop.json", "remm.json");
    recipeList.forEach(
        recipeFilename ->
            App.main(
                new String[] {
                  "--debug",
                  "database-build",
                  "--recipe",
                  getResource("db/chr1_1048426-1048726/%s".formatted(recipeFilename)).toString(),
                  "--output-dir",
                  dbDir.toString()
                }));
  }

  private String annotateVcf(String vcfResourceName) {
    ClassLoader classLoader = getClass().getClassLoader();
    Path inputVcfFile;
    try {
      inputVcfFile = Paths.get(classLoader.getResource(vcfResourceName).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }

    String[] args = {
      "annotate",
      "--annotations",
      dbDir.toString(),
      "--input",
      inputVcfFile.toString(),
      "--outputFormat",
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
      filePath = Paths.get(classLoader.getResource(name).toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return filePath;
  }
}
