package org.molgenis.vcf.annotate;

import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.molgenis.vcf.annotate.db2.exact.format.AnnotationDb;
import org.molgenis.vcf.annotate.db2.gnomad.GnomAdVcfAnnotator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class GnomAdAnnotatorBenchmark {

  private AnnotationDb annotationDb;

  @BeforeEach
  void beforeEach() throws IOException {
    annotationDb =
        new AnnotationDb(new File("C:\\Users\\Dennis Hendriksen\\Downloads\\gnomAd_chr21-22.zip"));
  }

  @AfterEach
  void afterEach() throws IOException {
    annotationDb.close();
  }

  @Fork(value = 1, warmups = 1)
  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  public void init(Blackhole blackhole) throws IOException {

    GnomAdVcfAnnotator vcfAnnotator = new GnomAdVcfAnnotator(annotationDb);

    try (VCFIterator vcfIterator =
        new VCFIteratorBuilder().open(VcfAnnotatorTest.class.getResourceAsStream("/vkgl.vcf"))) {
      while (vcfIterator.hasNext()) {
        blackhole.consume(vcfAnnotator.annotate(vcfIterator.next()));
      }
    }
  }
}
