package org.molgenis.vcf.annotate;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;
import java.io.*;
import org.molgenis.vcf.annotate.db.AnnotationDbReader;
import org.molgenis.vcf.annotate.db.model.GenomeAnnotationDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    long start;

    File inputFile = new File(args[0]);
    File outputFile = new File(args[1]);
    File dbFile = new File(args[2]);

    LOGGER.info("loading database ...");
    start = System.currentTimeMillis();
    GenomeAnnotationDb genomeAnnotationDb;
    try (FileInputStream fileInputStream = new FileInputStream(dbFile)) {
      genomeAnnotationDb = new AnnotationDbReader().readTranscriptDatabase(fileInputStream);
    }
    LOGGER.info("loading database done in {}ms", System.currentTimeMillis() - start);

    LOGGER.info("annotating vcf records ...");
    long records;
    try (VCFIterator vcfIterator = new VCFIteratorBuilder().open(new FileInputStream(inputFile));
        VariantContextWriter variantContextWriter =
            new VariantContextWriterBuilder()
                .setOutputFile(outputFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF)
                .unsetOption(Options.INDEX_ON_THE_FLY)
                .build()) {
      records = new VcfAnnotator(genomeAnnotationDb).annotate(vcfIterator, variantContextWriter);
    }
    LOGGER.info(
        "annotated {} vcf records done in {}ms", records, System.currentTimeMillis() - start);
  }
}
