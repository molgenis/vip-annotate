package org.molgenis.vipannotate.annotation.gnomad;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.annotation.gnomad.GnomAdAnnotation.Source;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class GnomAdAnnotator extends BaseVcfRecordAnnotator<GnomAdAnnotation> {
  private static final String INFO_ID_GNOMAD = "gnomAD";

  private final SequenceVariantAnnotationDb<SequenceVariant, GnomAdAnnotation> annotationDb;
  private boolean isNewAnnotation;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    isNewAnnotation =
        vcfMetaInfo.addOrUpdateInfo(
            INFO_ID_GNOMAD,
            "A",
            "String",
            "gnomAD v4.1.0 annotation formatted as 'SRC|AF|FAF95|FAF99|HN|QC|COV'; SRC=source (E=exomes, G=genomes, T=total), AF=allele frequency, FAF95=filtering allele frequency (95% confidence), FAF99=filtering allele frequency (99% confidence), HN=number of homozygotes, QC=quality control filters that failed, COV=coverage (percent of individuals in gnomAD source)",
            AppMetadata.getName(),
            AppMetadata.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.getChrom().getIdentifier().toString());
    int start = vcfRecord.getPos().get();
    int stop = vcfRecord.getPos().get() + vcfRecord.getRef().getBaseCount() - 1;
    Alt alt = vcfRecord.getAlt();
    List<AltAllele> altAlleles = alt.getAlleles();

    VcfInfoSubfieldValueBuilder infoSubfieldBuilder = getVcfInfoSubfieldBuilder();
    for (AltAllele altAllele : altAlleles) {
      List<GnomAdAnnotation> altAnnotations = getAltAnnotationList();
      annotationDb.findAnnotations(
          new SequenceVariant(
              contig,
              start,
              stop,
              altAllele,
              SequenceVariantTypeDetector.determineType(
                  vcfRecord.getRef().getBaseCount(), altAllele)),
          altAnnotations);

      appendAnnotations(altAnnotations, infoSubfieldBuilder);
    }

    writeInfoSubField(vcfRecord, INFO_ID_GNOMAD, infoSubfieldBuilder, isNewAnnotation);
  }

  private static void appendAnnotations(
      List<GnomAdAnnotation> annotations, VcfInfoSubfieldValueBuilder builder) {
    if (annotations.isEmpty()) {
      builder.appendValueMissing();
      return;
    }

    // FIXME throw exception in case of > 1 annotations, see SequenceVariantEncoderTest.encodeBig
    //        throw new RuntimeException(
    //            "%s:%d-%d %s>%s: Multiple AltAllele annotations found"
    //                .formatted(
    //                    contig.getName(),
    //                    start,
    //                    stop,
    //                    vcfRecord.getRef().getBases().toString(),
    //                    altAllele.get().toString()));
    GnomAdAnnotation altAnnotation = annotations.getFirst();
    appendAnnotation(altAnnotation, builder);
  }

  private static void appendAnnotation(
      GnomAdAnnotation annotation, VcfInfoSubfieldValueBuilder builder) {
    builder.startRawValue();

    appendAnnotationSource(annotation.source(), builder);
    builder.appendRaw('|');

    Double af = annotation.af();
    if (af != null) {
      builder.appendRaw(af, 4);
    }
    builder.appendRaw('|');

    builder.appendRaw(annotation.faf95(), 4);
    builder.appendRaw('|');

    builder.appendRaw(annotation.faf99(), 4);
    builder.appendRaw('|');

    builder.appendRaw(annotation.hn());
    builder.appendRaw('|');

    appendAnnotationFilters(annotation.filters(), builder);
    builder.appendRaw('|');

    builder.appendRaw(annotation.cov(), 4);
    builder.endRawValue();
  }

  private static void appendAnnotationSource(Source source, VcfInfoSubfieldValueBuilder builder) {
    builder.appendRaw(
        switch (source) {
          case GENOMES -> 'G';
          case EXOMES -> 'E';
          case TOTAL -> 'T';
        });
  }

  private static void appendAnnotationFilters(
      EnumSet<GnomAdAnnotation.Filter> filters, VcfInfoSubfieldValueBuilder builder) {
    if (filters.isEmpty()) {
      return;
    }

    int j = 0;
    for (Iterator<GnomAdAnnotation.Filter> iterator = filters.iterator(); iterator.hasNext(); ++j) {
      if (j > 0) {
        builder.appendRaw('&');
      }
      builder.appendRaw(
          switch (iterator.next()) {
            case AC0 -> "AC0";
            case AS_VQSR -> "AS_VQSR";
            case INBREEDING_COEFF -> "InbreedingCoeff";
          });
    }
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
