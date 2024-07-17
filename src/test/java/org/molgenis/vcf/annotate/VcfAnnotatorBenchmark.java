package org.molgenis.vcf.annotate;

import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.IOException;
import org.molgenis.vcf.annotate.db.AnnotationDbReader;
import org.molgenis.vcf.annotate.db.model.GenomeAnnotationDb;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class VcfAnnotatorBenchmark {

  @Fork(value = 1, warmups = 1)
  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  public void init(Blackhole blackhole) throws IOException {
    GenomeAnnotationDb genomeAnnotationDb =
        new AnnotationDbReader()
            .readTranscriptDatabase(VcfAnnotatorTest.class.getResourceAsStream("/db.ser"));
    VcfAnnotator vcfAnnotator = new VcfAnnotator(genomeAnnotationDb);

    try (VCFIterator vcfIterator =
        new VCFIteratorBuilder().open(VcfAnnotatorTest.class.getResourceAsStream("/vkgl.vcf"))) {
      while (vcfIterator.hasNext()) {
        blackhole.consume(vcfAnnotator.annotate(vcfIterator.next()));
      }
    }
  }
}
