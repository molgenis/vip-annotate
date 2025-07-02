package org.molgenis.vipannotate.annotation.gnomad;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfMetaInfo;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

@RequiredArgsConstructor
public class GnomAdAnnotator implements VcfRecordAnnotator {
  private static final String INFO_ID_GNOMAD_SRC = "gnomAD_SRC";
  private static final String INFO_ID_GNOMAD_AF = "gnomAD_AF";
  private static final String INFO_ID_GNOMAD_FAF95 = "gnomAD_FAF95";
  private static final String INFO_ID_GNOMAD_FAF99 = "gnomAD_FAF99";
  private static final String INFO_ID_GNOMAD_HN = "gnomAD_HN";
  private static final String INFO_ID_GNOMAD_QC = "gnomAD_QC";
  private static final String INFO_ID_GNOMAD_COV = "gnomAD_COV";

  private final SequenceVariantAnnotationDb<GnomAdAnnotation> annotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_SRC,
        "A",
        "Character",
        "gnomAD source: E=exomes, G=genomes, T=total",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_AF,
        "A",
        "Float",
        "gnomAD allele frequency",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_FAF95,
        "A",
        "Float",
        "gnomAD filtering allele frequency (95% confidence)",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_FAF99,
        "A",
        "Float",
        "gnomAD filtering allele frequency (99% confidence)",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_HN,
        "A",
        "Integer",
        "gnomAD number of homozygotes",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_QC,
        "A",
        "String",
        "gnomAD quality control filters that failed",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD_COV,
        "A",
        "Float",
        "gnomAD coverage (percent of individuals in gnomAD source)",
        App.getName(),
        App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    @Nullable String[] alts = vcfRecord.alt();

    List<@Nullable GnomAdAnnotation> altAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdAnnotation altAnnotation =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  contig,
                  start,
                  stop,
                  AltAlleleRegistry.get(alt),
                  SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt)));
      altAnnotations.add(altAnnotation);
    }

    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoString(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_SRC, this::mapSource);
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_AF, GnomAdAnnotation::af, "#.####");
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_FAF95, GnomAdAnnotation::faf95, "#.####");
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_FAF99, GnomAdAnnotation::faf99, "#.####");
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoInteger(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_HN, GnomAdAnnotation::hn);
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoString(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_QC, this::mapFilters);
    //noinspection DataFlowIssue
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord, altAnnotations, INFO_ID_GNOMAD_COV, GnomAdAnnotation::cov, "#.####");
  }

  private String mapSource(GnomAdAnnotation annotation) {
    return switch (annotation.source()) {
      case GENOMES -> "G";
      case EXOMES -> "E";
      case TOTAL -> "T";
    };
  }

  private @Nullable String mapFilters(GnomAdAnnotation annotation) {
    String filtersStr;

    EnumSet<GnomAdAnnotation.Filter> filters = annotation.filters();
    if (!filters.isEmpty()) {
      StringBuilder filtersStrBuilder = new StringBuilder();

      int j = 0;
      for (Iterator<GnomAdAnnotation.Filter> iterator = filters.iterator();
          iterator.hasNext();
          ++j) {
        if (j > 0) {
          filtersStrBuilder.append('&');
        }
        GnomAdAnnotation.Filter filter = iterator.next();
        String filterStr =
            switch (filter) {
              case AC0 -> "AC0";
              case AS_VQSR -> "AS_VQSR";
              case INBREEDING_COEFF -> "InbreedingCoeff";
            };
        filtersStrBuilder.append(filterStr);
      }

      filtersStr = filtersStrBuilder.toString();
    } else {
      filtersStr = null;
    }

    return filtersStr;
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
