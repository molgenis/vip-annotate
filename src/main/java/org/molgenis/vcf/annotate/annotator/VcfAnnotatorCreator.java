package org.molgenis.vcf.annotate.annotator;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.molgenis.vcf.annotate.annotator.gnomad.VcfRecordAnnotatorGnomAd;

public class VcfAnnotatorCreator {
  private VcfAnnotatorCreator() {}

  public static VcfAnnotator create(Path inputVcf, Path annotationsZip, Path outputVcf)
      throws IOException {
    VCFIterator vcfIterator = new VCFIteratorBuilder().open(createInputStream(inputVcf));
    List<VcfRecordAnnotator> vcfRecordAnnotators = createRecordAnnotators(annotationsZip);
    VariantContextWriter variantContextWriter =
        new VariantContextWriterBuilder()
            .setOutputVCFStream(createOutputStream(outputVcf))
            .unsetOption(Options.INDEX_ON_THE_FLY)
            .build();

    return new VcfAnnotator(vcfIterator, vcfRecordAnnotators, variantContextWriter);
  }

  private static List<VcfRecordAnnotator> createRecordAnnotators(Path annotationsZip) {
    // FIXME disabled effect annotator due to GraalVm issues
    //    VcfRecordAnnotator vcfRecordAnnotatorEffect =
    // VcfRecordAnnotatorEffect.create(annotationsZip);
    VcfRecordAnnotator vcfRecordAnnotatorGnomAd = VcfRecordAnnotatorGnomAd.create(annotationsZip);
    //    return List.of(vcfRecordAnnotatorEffect, vcfRecordAnnotatorGnomAd);
    return List.of(vcfRecordAnnotatorGnomAd);
  }

  private static InputStream createInputStream(Path inputVcfPath) throws IOException {
    InputStream inputStream;
    if (inputVcfPath != null) {
      inputStream = new BufferedInputStream(Files.newInputStream(inputVcfPath));
    } else {
      inputStream = new CloseIgnoringInputStream(System.in);
    }
    return inputStream;
  }

  private static OutputStream createOutputStream(Path outputVcfPath) throws IOException {
    OutputStream outputStream;
    if (outputVcfPath != null) {
      outputStream = new BufferedOutputStream(Files.newOutputStream(outputVcfPath), 1048576);
    } else {
      outputStream = new CloseIgnoringOutputStream(System.out);
    }
    return outputStream;
  }

  /** Helper class to ensure System.in is not closed */
  private static class CloseIgnoringInputStream extends FilterInputStream {
    public CloseIgnoringInputStream(InputStream inputStream) {
      super(inputStream);
    }

    @Override
    public void close() {
      // noop
    }
  }

  /** Helper class to ensure System.out is not closed */
  private static class CloseIgnoringOutputStream extends FilterOutputStream {
    public CloseIgnoringOutputStream(OutputStream outputStream) {
      super(outputStream);
    }

    @Override
    public void close() {
      // noop
    }
  }
}
