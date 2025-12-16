package org.molgenis.vipannotate.util;

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
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vipannotate.AppAnnotate;
import org.molgenis.vipannotate.AppDb;

@SuppressWarnings("DataFlowIssue")
public class AppDbAndAnnotateIT {
  private static final String EXPECTED_VCF_OUTPUT =
      """
              ##fileformat=VCFv4.5
              ##reference=file:///references/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna
              ##contig=<ID=chr1,length=248956422>
              ##INFO=<ID=FATHMM_MKL,NUMBER=A,TYPE=String,DESCRIPTION="FATHMM-MKL score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              ##INFO=<ID=gnomAD,NUMBER=A,TYPE=String,DESCRIPTION="gnomAD v4.1.0 annotation formatted as 'SRC|AF|FAF95|FAF99|HN|QC|COV'; SRC=source (E=exomes, G=genomes, T=total), AF=allele frequency, FAF95=filtering allele frequency (95% confidence), FAF99=filtering allele frequency (99% confidence), HN=number of homozygotes, QC=quality control filters that failed, COV=coverage (percent of individuals in gnomAD source)",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              ##INFO=<ID=ncER,NUMBER=A,TYPE=Float,DESCRIPTION="ncER score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              ##INFO=<ID=phyloP,NUMBER=A,TYPE=Float,DESCRIPTION="phyloP score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              ##INFO=<ID=REMM,NUMBER=A,TYPE=Float,DESCRIPTION="REMM score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              ##INFO=<ID=SpliceAI,NUMBER=A,TYPE=String,DESCRIPTION="SpliceAI annotations per ALT allele. Multiple annotations per allele are separated by '&'. Each annotation is formatted as 'NCBI_GENE_ID|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL' where DS stands for delta scores, DP stands for delta positions, AG for acceptor gain, AL for acceptor loss, DG for donor gain and DL for donor loss.",SOURCE="vip-annotate",VERSION="0.0.0-dev">
              #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
              chr1	1048426	.	G	A	.	.	FATHMM_MKL=0.133;gnomAD=T|0|0|0|0||0.7699;ncER=95.642;phyloP=-0.288;REMM=0.024;SpliceAI=375790|0|0|0|0||||
              chr1	1048426	.	G	GT	.	.	ncER=95.642;phyloP=-0.288;REMM=0.024;SpliceAI=375790|0|0|0|0||||
              chr1	1048426	.	GT	G	.	.	ncER=95.8983;phyloP=-0.288;REMM=0.043;SpliceAI=375790|0|0|0|0||||
              chr1	1048426	.	GTG	G	.	.	ncER=96.7879;phyloP=-0.288;REMM=0.402;SpliceAI=375790|0|0|0|0||||
              chr1	1048426	.	GTGG	G	.	.	ncER=98.2421;phyloP=-0.288;REMM=0.402;SpliceAI=375790|0|0|0|0||||
              chr1	1048426	.	GTGGG	G	.	.	ncER=98.2421;phyloP=-0.288;REMM=0.402;SpliceAI=375790|0|0|0|0||||
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
        () -> assertEquals(12613L, Files.size(dbDir.resolve("fathmmmkl.zip"))),
        () -> assertEquals(61967L, Files.size(dbDir.resolve("gnomad.zip"))),
        () -> assertEquals(8336L, Files.size(dbDir.resolve("ncer.zip"))),
        () -> assertEquals(8337L, Files.size(dbDir.resolve("phylop.zip"))),
        () -> assertEquals(8338L, Files.size(dbDir.resolve("remm.zip"))),
        () -> assertEquals(71848L, Files.size(dbDir.resolve("spliceai.zip"))),
        () -> assertEquals(EXPECTED_VCF_OUTPUT, vcf));
  }

  private void createDbs() {
    // fathmm_mkl
    AppDb.main(
        new String[] {
          "fathmm_mkl",
          "--input",
          getResource("db/chr1_1048426-1048726/GRCh38_FATHMM-MKL_NC.tsv.gz").toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--output",
          dbDir.resolve("fathmmmkl.zip").toString()
        });

    // gnomad
    AppDb.main(
        new String[] {
          "gnomad",
          "--input",
          getResource("db/chr1_1048426-1048726/gnomad.total.v4.1.sites.stripped-v3.tsv.gz")
              .toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--output",
          dbDir.resolve("gnomad.zip").toString()
        });

    // ncer
    AppDb.main(
        new String[] {
          "ncer",
          "--input",
          getResource("db/chr1_1048426-1048726/GRCh38_ncER_perc.bed.gz").toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--output",
          dbDir.resolve("ncer.zip").toString()
        });

    // phylop
    AppDb.main(
        new String[] {
          "phylop",
          "--input",
          getResource("db/chr1_1048426-1048726/hg38.phyloP100way.bed.gz").toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--output",
          dbDir.resolve("phylop.zip").toString()
        });

    // remm
    AppDb.main(
        new String[] {
          "remm",
          "--input",
          getResource("db/chr1_1048426-1048726/ReMM.v0.4.hg38.tsv.gz").toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--output",
          dbDir.resolve("remm.zip").toString()
        });

    // spliceai
    AppDb.main(
        new String[] {
          "spliceai",
          "--input",
          getResource("db/chr1_1048426-1048726/spliceai_scores.masked.hg38.vcf.gz").toString(),
          "--reference-index",
          getResource("db/chr1_1048426-1048726/GCA_000001405.15_chr1.fai").toString(),
          "--ncbi-gene-index",
          getResource("db/chr1_1048426-1048726/ncbi_gene.tsv").toString(),
          "--output",
          dbDir.resolve("spliceai.zip").toString()
        });
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
      "--annotations", dbDir.toString(), "--input", inputVcfFile.toString(), "--output", "-"
    };

    PrintStream originalOutputStream = System.out;
    String output;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (PrintStream outputStream = new PrintStream(byteArrayOutputStream, true, UTF_8)) {
      System.setOut(outputStream);
      try {
        AppAnnotate.main(args);
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
