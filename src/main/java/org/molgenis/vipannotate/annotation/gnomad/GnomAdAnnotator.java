package org.molgenis.vipannotate.annotation.gnomad;

import java.text.DecimalFormat;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfMetaInfo;
import org.molgenis.vipannotate.format.vcf.VcfRecord;
import org.molgenis.vipannotate.util.DecimalFormatRegistry;

@RequiredArgsConstructor
public class GnomAdAnnotator implements VcfRecordAnnotator {
  private static final String INFO_ID_GNOMAD = "gnomAD";
  private static final String INFO_ID_GNOMAD_SRC = "gnomAD_SRC";
  private static final String INFO_ID_GNOMAD_AF = "gnomAD_AF";
  private static final String INFO_ID_GNOMAD_FAF95 = "gnomAD_FAF95";
  private static final String INFO_ID_GNOMAD_FAF99 = "gnomAD_FAF99";
  private static final String INFO_ID_GNOMAD_HN = "gnomAD_HN";
  private static final String INFO_ID_GNOMAD_QC = "gnomAD_QC";
  private static final String INFO_ID_GNOMAD_COV = "gnomAD_COV";

  private final SequenceVariantAnnotationDb<SequenceVariant, GnomAdAnnotation> annotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;
  private @Nullable StringBuilder reusableStringBuilder;
  private @Nullable DecimalFormat reusableDecimalFormat;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_GNOMAD,
        "A",
        "String",
        "gnomAD v4.1.0 annotation formatted as 'SRC|AF|FAF95|FAF99|HN|QC|COV'; SRC=source (E=exomes, G=genomes, T=total), AF=allele frequency, FAF95=filtering allele frequency (95% confidence), FAF99=filtering allele frequency (99% confidence), HN=number of homozygotes, QC=quality control filters that failed, COV=coverage (percent of individuals in gnomAD source)",
        App.getName(),
        App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    @Nullable String[] alts = vcfRecord.alt();

    List<@Nullable GnomAdAnnotation> altsAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      List<GnomAdAnnotation> altAnnotations =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  contig,
                  start,
                  stop,
                  AltAlleleRegistry.get(alt),
                  SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt)));

      // only zero or one annotation per alt allowed
      if (altAnnotations.isEmpty()) {
        altsAnnotations.add(null);
      } else if (altAnnotations.size() == 1) {
        altsAnnotations.add(altAnnotations.getFirst());
      } else {
        throw new RuntimeException("Multiple AltAllele annotations found"); // TODO typed exception
      }
    }

    vcfRecordAnnotationWriter.writeInfoString(
        vcfRecord, altsAnnotations, INFO_ID_GNOMAD, this::createInfoAltString);
  }

  private @Nullable CharSequence createInfoAltString(@Nullable GnomAdAnnotation annotation) {
    if (reusableStringBuilder == null) {
      reusableStringBuilder = new StringBuilder();
    } else {
      reusableStringBuilder.setLength(0);
    }

    if (reusableDecimalFormat == null) {
      reusableDecimalFormat = DecimalFormatRegistry.getDecimalFormat("#.####");
    }

    if (annotation != null) {
      String filters = mapFilters(annotation);
      reusableStringBuilder.append(mapSource(annotation)).append('|');

      Double af = annotation.af();
      if (af != null) {
        reusableStringBuilder.append(reusableDecimalFormat.format(af));
      }

      reusableStringBuilder
          .append('|')
          .append(reusableDecimalFormat.format(annotation.faf95()))
          .append('|')
          .append(reusableDecimalFormat.format(annotation.faf99()))
          .append('|')
          .append(annotation.hn())
          .append('|');

      if (filters != null) {
        reusableStringBuilder.append(filters);
      }
      reusableStringBuilder.append('|').append(reusableDecimalFormat.format(annotation.cov()));
    } else {
      reusableStringBuilder.append('.');
    }

    return reusableStringBuilder;
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
