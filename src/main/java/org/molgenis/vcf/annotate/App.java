package org.molgenis.vcf.annotate;

import static org.molgenis.vcf.annotate.VcfAnnotator.NR_VAR_ALTS_ANNOTATED;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.*;
import java.io.*;
import org.molgenis.vcf.annotate.db.utils.AnnotationDbImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    File inputFile = new File(args[0]);
    File outputFile = new File(args[1]);
    File dbFile = new File(args[2]);

    LOGGER.info("annotating vcf records ...");
    long start = System.currentTimeMillis();
    long records;
    try (VCFIterator vcfIterator =
            new VCFIteratorBuilder().open(new BufferedInputStream(new FileInputStream(inputFile)));
        VariantContextWriter variantContextWriter =
            new VariantContextWriterBuilder()
                .setOutputFile(outputFile)
                .setOutputFileType(VariantContextWriterBuilder.OutputType.VCF)
                .unsetOption(Options.INDEX_ON_THE_FLY)
                .build()) {
      records =
          new VcfAnnotator(new AnnotationDbImpl(dbFile))
              .annotate(vcfIterator, variantContextWriter);
    }
    LOGGER.info(
        "annotated {} vcf records done in {}ms (annotated var-alts={})",
        records,
        System.currentTimeMillis() - start, NR_VAR_ALTS_ANNOTATED);
  }
}
