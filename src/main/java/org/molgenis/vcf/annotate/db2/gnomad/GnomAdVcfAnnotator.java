package org.molgenis.vcf.annotate.db2.gnomad;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.db.AnnotationDbReader;
import org.molgenis.vcf.annotate.db.model.AnnotationDb;
import org.molgenis.vcf.annotate.db.model.Chromosome;
import org.molgenis.vcf.annotate.db.model.GenomeAnnotationDb;
import org.molgenis.vcf.annotate.db.model.Transcript;
import org.molgenis.vcf.annotate.db2.exact.format.AnnotationDbImpl;
import org.molgenis.vcf.annotate.util.ContigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO make annotator interface
@RequiredArgsConstructor
public class GnomAdVcfAnnotator {
  private static final Logger LOGGER = LoggerFactory.getLogger(GnomAdVcfAnnotator.class);
  private AnnotationDbImpl annotationDb;
  private GenomeAnnotationDb genomeAnnotationDb;

  public GnomAdVcfAnnotator(AnnotationDbImpl annotationDb) {
    this.annotationDb = requireNonNull(annotationDb);

    LOGGER.info("loading transcript database ...");
    long start = System.currentTimeMillis();
    try (FileInputStream fileInputStream =
        new FileInputStream(
            "C:\\Users\\Dennis Hendriksen\\Dev\\vip-annotate\\src\\main\\resources\\db_20250307.zip")) {
      genomeAnnotationDb = new AnnotationDbReader().readTranscriptDatabase(fileInputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    LOGGER.info("loading transcript database done in {}ms", System.currentTimeMillis() - start);
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
      if (!vcfRecord.getContig().equals("chr1")) break;

      Allele referenceAllele = vcfRecord.getReference();
      if (referenceAllele.isSymbolic()
          || referenceAllele.isSingleBreakend()
          || referenceAllele.isBreakpoint()) {
        continue; // FIXME support
      }
      boolean canAnnotateAlternateAlleles = true;
      for (Allele alternateAllele : vcfRecord.getAlternateAlleles()) {
        if (alternateAllele.isSymbolic()
            || alternateAllele.isSingleBreakend()
            || alternateAllele.isBreakpoint()) {
          canAnnotateAlternateAlleles = false;
          break;
        }
      }
      if (!canAnnotateAlternateAlleles) {
        continue; // FIXME support
      }

      VariantContext annotatedVcfRecord = annotate(vcfRecord);
      writer.add(annotatedVcfRecord);
      if (records > 0 && records % 100000 == 0) LOGGER.info("processed {} records", records);
      records++;
    }
    return records;
  }

  public static int NR_RECORDS = 0;
  public static int NR_RECORDS_HIT = 0;
  public static int NR_RECORDS_LIVE = 0;
  public static int NR_RECORDS_NOOP = 0;

  public VariantContext annotate(VariantContext vcfRecord) {
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
                NR_RECORDS_HIT++;

                GnomAdShortVariantAnnotation gnomAdAnnotation =
                    GnomAdShortVariantAnnotationCodec.decode(memoryBuffer);

                GnomAdShortVariantAnnotation.VariantData variantData = gnomAdAnnotation.getJoint();
                if (variantData == null) variantData = gnomAdAnnotation.getGenomes();
                if (variantData == null) variantData = gnomAdAnnotation.getExomes();

                altAnnotation = variantData.getAf();
              } else {
                altAnnotation = null;

                Chromosome chromosome;
                try {
                  chromosome = ContigUtils.map(vcfRecord.getContig());
                } catch (Exception e) {
                  chromosome = null;
                }
                AnnotationDb annotationDb1 = genomeAnnotationDb.get(chromosome);
                List<Transcript> overlapTranscripts =
                    annotationDb1 != null
                        ? annotationDb1.findOverlapTranscripts(
                            vcfRecord.getStart(), vcfRecord.getEnd())
                        : Collections.emptyList();
                if (!overlapTranscripts.isEmpty()) {
                  NR_RECORDS_LIVE++;
                } else {
                  NR_RECORDS_NOOP++;
                }
              }

              altAfAnnotations.add(altAnnotation);
            });

    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(vcfRecord);
    variantContextBuilder.attribute("gnomAD_AF", altAfAnnotations);
    return variantContextBuilder.make();
  }
}
