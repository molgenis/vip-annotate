package org.molgenis.vcf.annotate.annotator.gnomad;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.fury.memory.MemoryBuffer;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.db.effect.model.Chromosome;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationDbImpl;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdShortVariantAnnotation;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdShortVariantAnnotationCodec;
import org.molgenis.vcf.annotate.util.ContigUtils;

@RequiredArgsConstructor
public class VcfRecordAnnotatorGnomAd implements VcfRecordAnnotator {
  @NonNull private final AnnotationDbImpl annotationDb;

  @Override
  public void updateHeader(VCFHeader vcfHeader) {
    vcfHeader.addMetaDataLine(
        new VCFInfoHeaderLine(
            "gnomAD_AF",
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Float,
            "VIP annotate PoC: gnomAD AF"));
  }

  @Override
  public void annotate(VariantContext vcfRecord, VariantContextBuilder vcfRecordBuilder) {
    Chromosome chromosome = ContigUtils.map(vcfRecord.getContig());
    if (chromosome == null) {
      return;
    }

    List<Double> altAfAnnotations = new ArrayList<>(vcfRecord.getNAlleles() - 1);
    vcfRecord
        .getAlternateAlleles()
        .forEach(
            alternate -> {
              MemoryBuffer memoryBuffer =
                  annotationDb.findVariant(
                      chromosome.getId(),
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

    if (altAfAnnotations.stream().anyMatch(Objects::nonNull)) {
      vcfRecordBuilder.attribute("gnomAD_AF", altAfAnnotations);
    }
  }

  public static VcfRecordAnnotatorGnomAd create(Path annotationsZip) {
    AnnotationDbImpl annotationDb = new AnnotationDbImpl(annotationsZip);
    return new VcfRecordAnnotatorGnomAd(annotationDb);
  }
}
