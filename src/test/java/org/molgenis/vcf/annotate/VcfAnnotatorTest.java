package org.molgenis.vcf.annotate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.annotate.db.AnnotationDbReader;
import org.molgenis.vcf.annotate.db.model.GenomeAnnotationDb;

class VcfAnnotatorTest {
  private static GenomeAnnotationDb genomeAnnotationDb;
  private VcfAnnotator vcfAnnotator;

  @BeforeAll
  static void beforeAll() throws IOException {
    genomeAnnotationDb =
        new AnnotationDbReader()
            .readTranscriptDatabase(VcfAnnotatorTest.class.getResourceAsStream("/db.ser"));
  }

  @BeforeEach
  void beforeEach() {
    vcfAnnotator = new VcfAnnotator(genomeAnnotationDb);
  }

  @Test
  void annotateMinStrand() throws IOException {
    String vcf =
        """
##fileformat=VCFv4.2
##contig=<ID=chr1,length=248956422>
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr1	1790454	.	G	A	.	.	.
""";

    String expectedVcf =
        """
##fileformat=VCFv4.2
##INFO=<ID=CSQ,Number=.,Type=String,Description="Consequence annotations from VIP. Format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|ALLELE_NUM|STRAND">
##contig=<ID=chr1,length=248956422>
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr1	1790454	.	G	A	.	.	CSQ=A|stop_gained|HIGH|GNB1|4396|transcript|NM_001282539.2|protein_coding|0|0,A|stop_gained|HIGH|GNB1|4396|transcript|NM_001282538.2|protein_coding|0|0,A|stop_gained|HIGH|GNB1|4396|transcript|NM_002074.5|protein_coding|0|0
    """;

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try (VCFIterator vcfIterator =
            new VCFIteratorBuilder().open(new ByteArrayInputStream(vcf.getBytes(UTF_8)));
        VariantContextWriter variantContextWriter =
            new VariantContextWriterBuilder()
                .setOutputVCFStream(byteArrayOutputStream)
                .unsetOption(Options.INDEX_ON_THE_FLY)
                .build()) {
      vcfAnnotator.annotate(vcfIterator, variantContextWriter);
    }
    String annotateVcfStr = byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    System.out.println(annotateVcfStr);
    assertEquals(expectedVcf, annotateVcfStr);
  }
}
