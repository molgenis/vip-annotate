package org.molgenis.vcf.annotate;

import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.VariantContext;
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
    GenomeAnnotationDb genomeAnnotationDb = new AnnotationDbReader().readTranscriptDatabase(dbFile);
    LOGGER.info("loading database done in {}ms", System.currentTimeMillis() - start);

    LOGGER.info("annotating vcf records ...");
    start = System.currentTimeMillis();
    VariantContextAnnotator variantContextAnnotator =
        new VariantContextAnnotator(genomeAnnotationDb);

    try (VariantContextWriter writer =
            new VariantContextWriterBuilder()
                .setOutputFile(outputFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF)
                .unsetOption(Options.INDEX_ON_THE_FLY)
                .build();
        AbstractFeatureReader<VariantContext, LineIterator> reader =
            AbstractFeatureReader.getFeatureReader(
                inputFile.getAbsolutePath(), new VCFCodec(), false)) {

      VCFHeader vcfHeader = (VCFHeader) reader.getHeader();
      vcfHeader.addMetaDataLine(
          new VCFInfoHeaderLine(
              "CSQ",
              VCFHeaderLineCount.UNBOUNDED,
              VCFHeaderLineType.String,
              "Consequence annotations from VIP. Format: Allele|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|ALLELE_NUM|STRAND"));
      writer.writeHeader(vcfHeader);

      long records = 0;
      for (VariantContext variantContext : reader.iterator()) {
        VariantContext annotatedVariantContext = variantContextAnnotator.annotate(variantContext);
        writer.add(annotatedVariantContext);
        records++;
      }
      LOGGER.info(
          "annotated {} vcf records done in {}ms", records, System.currentTimeMillis() - start);
    }
  }
}
