package org.molgenis.vipannotate.annotation.gnomadshortvariant;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.AnnotationDb;
import org.molgenis.vipannotate.annotation.Variant;
import org.molgenis.vipannotate.annotation.VcfRecordAnnotator;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

public class GnomAdShortVariantAnnotator implements VcfRecordAnnotator {
  private final AnnotationDb<GnomAdShortVariantAnnotationData> annotationDb;
  private final DecimalFormat decimalFormat;

  public GnomAdShortVariantAnnotator(
      @NonNull AnnotationDb<GnomAdShortVariantAnnotationData> annotationDb) {
    this.annotationDb = annotationDb;
    this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    this.decimalFormat.applyPattern("#.####");
  }

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader
        .vcfMetaInfo()
        .addOrUpdateInfo("gnomAD_AF", "A", "Float", "gnomAD AF", App.getName(), App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    String chromosome = vcfRecord.chrom();
    String[] alts = vcfRecord.alt();
    List<Double> altAfAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdShortVariantAnnotationData gnomAdAnnotation =
          annotationDb.findAnnotations(
              new Variant(
                  chromosome,
                  Math.toIntExact(vcfRecord.pos()), // FIXME annotationDb should accept long?
                  Math.toIntExact(
                      vcfRecord.pos()
                          + vcfRecord.ref().length()
                          - 1), // FIXME annotationDb should accept long?
                  alt.getBytes(StandardCharsets.UTF_8)));

      Double altAnnotation;
      if (gnomAdAnnotation != null) {
        altAnnotation = gnomAdAnnotation.af();
        // FIXME add other annotations
      } else {
        altAnnotation = null;
      }

      altAfAnnotations.add(altAnnotation);
    }

    if (altAfAnnotations.stream().anyMatch(Objects::nonNull)) {
      StringBuilder builder = new StringBuilder();
      for (Double altAnnotation : altAfAnnotations) {
        if (altAnnotation != null) {
          builder.append(decimalFormat.format(altAnnotation));
        } else {
          builder.append('.');
        }
      }
      vcfRecord.info().put("gnomAD_AF", builder.toString());
    } else {
      vcfRecord.info().remove("gnomAD_AF");
    }
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
