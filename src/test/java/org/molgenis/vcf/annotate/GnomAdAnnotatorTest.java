package org.molgenis.vcf.annotate;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.annotate.db2.exact.format.AnnotationDbImpl;
import org.molgenis.vcf.annotate.db2.gnomad.GnomAdVcfAnnotator;

class GnomAdAnnotatorTest {
  private AnnotationDbImpl annotationDb;
  private GnomAdVcfAnnotator vcfAnnotator;

  @BeforeEach
  void beforeEach() throws IOException {
    annotationDb =
        new AnnotationDbImpl(
            new File("C:\\Users\\Dennis Hendriksen\\Downloads\\gnomAd_chr21-22_v14.zip"));
    vcfAnnotator = new GnomAdVcfAnnotator(annotationDb);
  }

  @AfterEach
  void afterEach() throws IOException {
    annotationDb.close();
  }

  @Test
  void annotate() throws IOException {
    for (int j = 0; j < 1; ++j) {
      long start = System.currentTimeMillis();
      try (VCFIterator vcfIterator =
          //        new
          // VCFIteratorBuilder().open(GnomAdAnnotatorTest.class.getResourceAsStream("/vkgl.vcf")))
          // {
          new VCFIteratorBuilder()
              .open(
                  new File(
                      "C:\\Users\\Dennis Hendriksen\\Downloads\\hlhs_wgs_104_snv.vcf\\hlhs_wgs_104_snv.vcf"))) {

        for (int i = 0; vcfIterator.hasNext(); ++i) {
          VariantContext variantContext = vcfIterator.next();
          VariantContext annotatedVariantContext = vcfAnnotator.annotate(variantContext);
        }
      }
      long end = System.currentTimeMillis();
      System.out.println(
          "annotated "
              + GnomAdVcfAnnotator.NR_RECORDS
              + " records in "
              + (end - start)
              + "ms --> "
              + ((GnomAdVcfAnnotator.NR_RECORDS * 1000L) / (end - start))
              + " records/s");
      GnomAdVcfAnnotator.NR_RECORDS = 0;
    }
  }
}
