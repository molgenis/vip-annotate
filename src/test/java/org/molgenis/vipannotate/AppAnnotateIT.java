package org.molgenis.vipannotate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class AppAnnotateIT {
  @Test
  public void testAnnotate() throws URISyntaxException {
    ClassLoader classLoader = getClass().getClassLoader();
    @SuppressWarnings("DataFlowIssue")
    Path inputVcfFile =
        Paths.get((classLoader.getResource("annotate/chr2_bin633/input.vcf")).toURI());

    // FIXME replace production annotation database with test annotation database with chr2 bin633
    //    @SuppressWarnings("DataFlowIssue")
    //    Path annotationsDir =
    // Paths.get((classLoader.getResource("annotate/annotations")).toURI());
    @SuppressWarnings("DataFlowIssue")
    Path annotationsDir = Path.of(System.getenv("ANNOTATIONS_DIR"));

    String[] args = {
      "--input", inputVcfFile.toString(), "--annotations-dir", annotationsDir.toString()
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

    String expectedOutput =
"""
##fileformat=VCFv4.5
##reference=file:///references/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna
##contig=<ID=chr2,length=242193529>
##INFO=<ID=gnomAD_SRC,NUMBER=A,TYPE=Character,DESCRIPTION="gnomAD source: E=exomes, G=genomes, T=total",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_AF,NUMBER=A,TYPE=Float,DESCRIPTION="gnomAD allele frequency",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_FAF95,NUMBER=A,TYPE=Float,DESCRIPTION="gnomAD filtering allele frequency (95% confidence)",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_FAF99,NUMBER=A,TYPE=Float,DESCRIPTION="gnomAD filtering allele frequency (99% confidence)",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_HN,NUMBER=A,TYPE=Integer,DESCRIPTION="gnomAD number of homozygotes",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_QC,NUMBER=A,TYPE=String,DESCRIPTION="gnomAD quality control filters that failed",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=gnomAD_COV,NUMBER=A,TYPE=Float,DESCRIPTION="gnomAD coverage (percent of individuals in gnomAD source)",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=ncER,NUMBER=1,TYPE=Float,DESCRIPTION="ncER score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=phyloP,NUMBER=1,TYPE=Float,DESCRIPTION="phyloP score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=REMM,NUMBER=1,TYPE=Float,DESCRIPTION="REMM score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=SpliceAI,NUMBER=A,TYPE=String,DESCRIPTION="SpliceAI annotations per ALT allele. Multiple annotations per allele are separated by '&'. Each annotation is formatted as 'NCBI_GENE_ID|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL' where DS stands for delta scores, DP stands for delta positions, AG for acceptor gain, AL for acceptor loss, DG for donor gain and DL for donor loss.",SOURCE="vip-annotate",VERSION="0.0.0-dev">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr2	166305791	.	C	T	.	.	gnomAD_SRC=T;gnomAD_AF=0;gnomAD_FAF95=0;gnomAD_FAF99=0;gnomAD_HN=0;gnomAD_COV=0.9993;ncER=21.1753;phyloP=9.995;REMM=0.988;SpliceAI=6335|0|0|0|0.99|0.0|0.0|0.0
""";
    assertEquals(expectedOutput, output);
  }
}
