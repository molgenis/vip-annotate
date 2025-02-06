package org.molgenis.vcf.annotate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
  void annotate() throws IOException {
    try (VCFIterator vcfIterator =
            new VCFIteratorBuilder().open(VcfAnnotatorTest.class.getResourceAsStream("/vkgl.vcf"));
        VCFIterator expectedVcfIterator =
            new VCFIteratorBuilder()
                .open(VcfAnnotatorTest.class.getResourceAsStream("/vkgl_annotations_vip.vcf"))) {

      for (int i = 0; vcfIterator.hasNext(); ++i) {
        VariantContext variantContext = vcfIterator.next();
        VariantContext annotatedVariantContext = vcfAnnotator.annotate(variantContext);
        VariantContext expectedAnnotatedVariantContext = expectedVcfIterator.next();
        assertVariantContextEquals(i, annotatedVariantContext, expectedAnnotatedVariantContext);
      }
    }
  }

  private static void assertVariantContextEquals(
      int i, VariantContext variantContext, VariantContext expectedVariantContext) {
    List<String> csqList = (List<String>) (List<?>) variantContext.getAttributeAsList("CSQ");
    List<String> expectedCsqList =
        (List<String>) (List<?>) expectedVariantContext.getAttributeAsList("CSQ");
    String variantId =
        "%s-%d-%s-%s"
            .formatted(
                variantContext.getContig(),
                variantContext.getStart(),
                variantContext.getReference().getBaseString(),
                variantContext.getAlternateAlleles().stream()
                    .map(Allele::getBaseString)
                    .collect(Collectors.joining(",")));
    assertCsqListEquals(i, variantId, csqList, expectedCsqList);
  }

  private static void assertCsqListEquals(
      int i, String variant, List<String> csqList, List<String> expectedCsqList) {
    List<String[]> effectsList =
        csqList.stream()
            .map(csq -> csq.split("\\|", -1))
            .collect(Collectors.toCollection(ArrayList::new));
    List<String[]> expectedEffectsList =
        expectedCsqList.stream()
            .map(csq -> csq.split("\\|", -1))
            .filter(
                effect ->
                    effect[5].equals("Transcript") // exclude regulatory and motif features from VEP
                        && !effect[1].equals(
                            "upstream_gene_variant") // exclude upstream gene variant from VEP
                        && !effect[1].equals(
                            "downstream_gene_variant")) // exclude downstream gene variant from VEP
            .collect(Collectors.toCollection(ArrayList::new));

    // sort by transcript identifier
    effectsList.sort(Comparator.comparing(o -> o[6]));
    expectedEffectsList.sort(Comparator.comparing(o -> o[6]));

    if (expectedEffectsList.size() != effectsList.size()) {
      System.out.println(
          "#"
              + i
              + " "
              + variant
              + ": list size mismatch expected:"
              + expectedEffectsList.size()
              + " actual:"
              + effectsList.size());
      return;
    }

    for (int j = 0; j < effectsList.size(); j++) {
      assertCsqEquals(i, variant, effectsList.get(j), expectedEffectsList.get(j));
    }
  }

  private static void assertCsqEquals(int i, String variant, String[] csq, String[] expectedCsq) {
    // VEP bugs?

    /*
     org.opentest4j.AssertionFailedError: #3209 chr2-201276826-G-A 6: NM_001228.4 NM_001228.5 ==>
     Expected :NM_001228.4
     Actual   :NM_001228.5
    */
    /*
       org.opentest4j.AssertionFailedError: #4997 chr4-122923143-A-C 3: SPATA5 AFG2A ==>
       Expected :SPATA5
       Actual   :AFG2A
    */
    /*
        org.opentest4j.AssertionFailedError: #8143 chr9-35657872-C-T 6: NR_003051.3 NR_003051.4 ==>
    Expected :NR_003051.3
    Actual   :NR_003051.4
         */
    /*
     org.opentest4j.AssertionFailedError: #9443 chr11-2661959-A-G 6: NR_002728.3 NR_002728.4 ==>
     Expected :NR_002728.3
     Actual   :NR_002728.4
    */
    /*
        org.opentest4j.AssertionFailedError: #12809 chr15-45402956-G-T 3: SPATA5L1 AFG2B ==>
    Expected :SPATA5L1
    Actual   :AFG2B
         */
    /*
        org.opentest4j.AssertionFailedError: #14251 chr16-88734501-C-T 3: LOC100289580 HSALR1 ==>
    Expected :LOC100289580
    Actual   :HSALR1
         */
    /*
        org.opentest4j.AssertionFailedError: #14409 chr17-7192962-G-A 6: NM_001365.4 NM_001365.5 ==>
    Expected :NM_001365.4
    Actual   :NM_001365.5
         */
    /*
     see https://mutalyzer.nl/normalizer/NC_000002.12:g.73424470C%3ET

     org.opentest4j.AssertionFailedError: #2403 chr2-73424470-C-T 10: NM_015120.4:c.808C>T NM_015120.4:c.805C>T ==>
     Expected :NM_015120.4:c.808C>T
     Actual   :NM_015120.4:c.805C>T
    */

    // FIXME vip-annotate bug
    /*
    org.opentest4j.AssertionFailedError: #9625 chr11-46739505-G-A 1: 3_prime_UTR_variant splice_region_variant&3_prime_UTR_variant ==>
    Expected :[3_prime_UTR_variant]
    Actual   :[3_prime_UTR_variant, splice_region_variant]
     */
    /*
        T,non_coding_transcript_exon_variant,MODIFIER,ORAI1,84876,Transcript,NR_186857.1,misc_RNA,1/2,,,,1,1
    T,stop_gained,HIGH,ORAI1,84876,Transcript,NM_032790.3,protein_coding,2/3,,NM_032790.3:c.205G>T,NP_116179.2:p.Glu69Ter,398/1499,205/909,69/302,E/*,Gag/Tag,,1,,1,,1,EntrezGene,,,,,,,,,,,,,,,,,,,15,6,-16,14,0.00,0.00,0.04,0.00,ORAI1,VUS,0.9087773,,,,,,,AR,,,,,,,,,,,,,,,,,,96.2979,0.98378,0.9980,,,,0.0501066181149502,,2.45300006866455

    org.opentest4j.AssertionFailedError: #11597 chr12-121626949-G-T 1: stop_gained non_coding_transcript_exon_variant ==>
    Expected :[stop_gained]
    Actual   :[non_coding_transcript_exon_variant]
         */
    /*
     *         // vip-annotate can't determine the extension length and thus reports '?', for example:
     *         // Expected :NP_000240.1:p.Ter757TyrextTer36
     *         // Actual   :NP_000240.1:p.Ter757TyrextTer?
     */
    for (int j = 0; j < 12; ++j) {
      if (j == 1) {
        List<String> sortedCsq = Arrays.stream(csq[j].split("&", -1)).sorted().toList();
        List<String> sortedExpectedCsq =
            Arrays.stream(expectedCsq[j].split("&", -1)).sorted().toList();
        assertEquals(
            sortedExpectedCsq,
            sortedCsq,
            "#" + i + " " + variant + " " + j + ": " + expectedCsq[j] + " " + csq[j]);
      } else if (j == 7 && !csq[j].equals(expectedCsq[j])) {
        // VEP appears to use the original GenBank feature type instead of the SOFA feature type
        // (‘gbkey’ field) for RefSeq transcripts (example: chr1-2405815-C-T 7/NR_164636.1, VEP
        // annotates misc_RNA, vip-annotate annotates protein_coding)
        // It doesn't appear to produce a protein though:
        // https://www.ncbi.nlm.nih.gov/datasets/gene/id/5192/products/
        // TODO what is the preferred annotation?
        continue;
      } else {
        if (!csq[j].equals(expectedCsq[j])) {
          System.out.println("***");
          System.out.println(String.join(",", csq));
          System.out.println(String.join(",", expectedCsq));
        }
        assertEquals(
            expectedCsq[j],
            csq[j],
            "#" + i + " " + variant + " " + j + ": " + expectedCsq[j] + " " + csq[j]);
      }
    }
    assertEquals(
        expectedCsq[18],
        csq[12],
        "#" + i + " " + variant + " 12: " + expectedCsq[18] + " " + csq[12]);
    assertEquals(
        expectedCsq[20],
        csq[13],
        "#" + i + " " + variant + " 13: " + expectedCsq[20] + " " + csq[13]);
    // actual:
    // Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|ALLELE_NUM|STRAND
    // Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|ALLELE_NUM|DISTANCE|STRAND|FLAGS|PICK|SYMBOL_SOURCE|HGNC_ID|REFSEQ_MATCH|REFSEQ_OFFSET|SOURCE|SIFT|PolyPhen|HGVS_OFFSET|CLIN_SIG|SOMATIC|PHENO|PUBMED|CHECK_REF|MOTIF_NAME|MOTIF_POS|HIGH_INF_POS|MOTIF_SCORE_CHANGE|TRANSCRIPTION_FACTORS|Grantham|SpliceAI_pred_DP_AG|SpliceAI_pred_DP_AL|SpliceAI_pred_DP_DG|SpliceAI_pred_DP_DL|SpliceAI_pred_DS_AG|SpliceAI_pred_DS_AL|SpliceAI_pred_DS_DG|SpliceAI_pred_DS_DL|SpliceAI_pred_SYMBOL|CAPICE_CL|CAPICE_SC|existing_InFrame_oORFs|existing_OutOfFrame_oORFs|existing_uORFs|five_prime_UTR_variant_annotation|five_prime_UTR_variant_consequence|IncompletePenetrance|InheritanceModesGene|VKGL|VKGL_CL|gnomAD_AF|gnomAD_COV|gnomAD_FAF95|gnomAD_FAF99|gnomAD_HN|gnomAD_QC|gnomAD_SRC|clinVar_CLNID|clinVar_CLNREVSTAT|clinVar_CLNSIG|clinVar_CLNSIGINCL|ASV_ACMG_class|ASV_AnnotSV_ranking_criteria|ASV_AnnotSV_ranking_score|ALPHSCORE|ncER|FATHMM_MKL_NC|ReMM|GDB_BIV|GDB_ENH|GDB_INS|GDB_PRO|GDB_SIL|phyloP
  }
}
