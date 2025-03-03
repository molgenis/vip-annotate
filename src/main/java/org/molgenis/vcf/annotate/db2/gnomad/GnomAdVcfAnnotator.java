package org.molgenis.vcf.annotate.db2.gnomad;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db2.exact.format.AnnotationDbImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO make annotator interface
@RequiredArgsConstructor
public class GnomAdVcfAnnotator {
  private static final Logger LOGGER = LoggerFactory.getLogger(GnomAdVcfAnnotator.class);
  private AnnotationDbImpl annotationDb;

  public GnomAdVcfAnnotator(AnnotationDbImpl annotationDb) {
    this.annotationDb = requireNonNull(annotationDb);
  }

  public long annotate(VCFIterator reader, VariantContextWriter writer) throws IOException {
    VCFHeader vcfHeader = reader.getHeader();
    vcfHeader.addMetaDataLine(
        new VCFInfoHeaderLine(
            "gnomAD_AF",
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Float,
            "VIP annotate PoC: gnomAD AF"));
    writer.writeHeader(vcfHeader);

    long records = 0;
    while (reader.hasNext()) {
      VariantContext vcfRecord = reader.next();
      VariantContext annotatedVcfRecord = annotate(vcfRecord);
      writer.add(annotatedVcfRecord);
      if (records > 0 && records % 100000 == 0) LOGGER.info("processed {} records", records);
      records++;
    }
    return records;
  }

  public static int NR_RECORDS = 0;

  public VariantContext annotate(VariantContext vcfRecord) {
    if (!vcfRecord.getContig().equals("chr21") && !vcfRecord.getContig().equals("chr22"))
      return vcfRecord; // FIXME

    ++NR_RECORDS;

    List<Double> altAfAnnotations = new ArrayList<>(vcfRecord.getNAlleles() - 1);
    vcfRecord
        .getAlternateAlleles()
        .forEach(
            alternate -> {
              MemoryBuffer memoryBuffer =
                  annotationDb.findVariant(
                      vcfRecord.getContig(),
                      vcfRecord.getStart(),
                      vcfRecord.getEnd(),
                      alternate.getBases());

              Double altAnnotation;
              if (memoryBuffer != null) {
                GnomAdShortVariantAnnotation gnomAdAnnotation =
                    GnomAdShortVariantAnnotationCodec.decode(memoryBuffer);

                GnomAdShortVariantAnnotation.VariantData variantData = gnomAdAnnotation.getJoint();
                if (variantData == null) variantData = gnomAdAnnotation.getGenomes();
                if (variantData == null) variantData = gnomAdAnnotation.getExomes();

                altAnnotation = variantData.getAf();
              } else {
                altAnnotation = null;
              }

              altAfAnnotations.add(altAnnotation);
            });

    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(vcfRecord);
    variantContextBuilder.attribute("gnomAD_AF", altAfAnnotations);
    return variantContextBuilder.make();
  }
}
