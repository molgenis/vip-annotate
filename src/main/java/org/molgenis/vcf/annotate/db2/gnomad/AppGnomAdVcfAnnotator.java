package org.molgenis.vcf.annotate.db2.gnomad;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.*;
import org.molgenis.vcf.annotate.db2.exact.format.AnnotationDbImpl;

class AppGnomAdVcfAnnotator {
  public static void main(String[] args) throws IOException {
    File inputVcfFile = new File(args[0]);
    File inputDbFile = new File(args[1]);
    File outputFile = new File(args[2]);

    GnomAdVcfAnnotator vcfAnnotator = new GnomAdVcfAnnotator(new AnnotationDbImpl(inputVcfFile));

    long start = System.currentTimeMillis();
    try (VCFIterator vcfIterator = new VCFIteratorBuilder().open(inputDbFile);
        VariantContextWriter variantContextWriter =
            new VariantContextWriterBuilder()
                .setOutputFile(outputFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF)
                .unsetOption(Options.INDEX_ON_THE_FLY)
                .build()) {
      vcfAnnotator.annotate(vcfIterator, variantContextWriter);
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
  }
}
