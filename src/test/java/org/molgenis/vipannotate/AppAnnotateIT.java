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
        Paths.get((classLoader.getResource("annotate/chr2_166195185-166375987/input.vcf")).toURI());

    @SuppressWarnings("DataFlowIssue")
    Path annotationsDir =
        Paths.get(
            (classLoader.getResource("annotate/chr2_166195185-166375987/annotations")).toURI());

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
    // TODO repair test after GnomAdTsvRecordToGnomAdAnnotatedSequenceVariantMapper fixme is
    // resolved
    String expectedOutput =
"""
##fileformat=VCFv4.5
##reference=file:///references/GCA_000001405.15_GRCh38_no_alt_analysis_set.fna
##contig=<ID=chr2,length=242193529>
##INFO=<ID=gnomAD,NUMBER=A,TYPE=String,DESCRIPTION="gnomAD v4.1.0 annotation formatted as 'SRC|AF|FAF95|FAF99|HN|QC|COV'; SRC=source (E=exomes, G=genomes, T=total), AF=allele frequency, FAF95=filtering allele frequency (95% confidence), FAF99=filtering allele frequency (99% confidence), HN=number of homozygotes, QC=quality control filters that failed, COV=coverage (percent of individuals in gnomAD source)",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=ncER,NUMBER=A,TYPE=Float,DESCRIPTION="ncER score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=phyloP,NUMBER=A,TYPE=Float,DESCRIPTION="phyloP score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=REMM,NUMBER=A,TYPE=Float,DESCRIPTION="REMM score",SOURCE="vip-annotate",VERSION="0.0.0-dev">
##INFO=<ID=SpliceAI,NUMBER=A,TYPE=String,DESCRIPTION="SpliceAI annotations per ALT allele. Multiple annotations per allele are separated by '&'. Each annotation is formatted as 'NCBI_GENE_ID|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL' where DS stands for delta scores, DP stands for delta positions, AG for acceptor gain, AL for acceptor loss, DG for donor gain and DL for donor loss.",SOURCE="vip-annotate",VERSION="0.0.0-dev">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr2	166305791	.	C	T	.	.	gnomAD=T|0|0|0|0||1;ncER=99.8932;phyloP=9.995;REMM=0.988;SpliceAI=6335|0|0|0|0.99|-3|-5|-3|1
chr2	166305792	.	G	A,C	.	.	gnomAD=T|0|0|0|0||1,T|0|0|0|0||1;ncER=99.9313,99.9313;phyloP=9.996,9.996;REMM=0.992,0.992;SpliceAI=6335|0|0|0.03|0|-15|-7|2|0,6335|0|0|0|0|-1|-6|0|-4
""";
    assertEquals(expectedOutput, output);
  }
}
