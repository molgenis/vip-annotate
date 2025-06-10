package org.molgenis.vipannotate.annotator.gnomad;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.db.exact.format.AnnotationDbImpl;
import org.molgenis.vipannotate.db.gnomad.GnomAdShortVariantAnnotation;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

public class GnomAdAnnotator implements VcfRecordAnnotator {
  private final AnnotationDbImpl<GnomAdShortVariantAnnotation> annotationDb;
  private final DecimalFormat decimalFormat;

  public GnomAdAnnotator(@NonNull AnnotationDbImpl<GnomAdShortVariantAnnotation> annotationDb) {
    this.annotationDb = annotationDb;
    this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    this.decimalFormat.applyPattern("#.####");
  }

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    // FIXME version
    vcfHeader
        .vcfMetaInfo()
        .addOrUpdateInfo("gnomAD_AF", "A", "Float", "gnomAD AF", "VIP", "0.0.0-dev");
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    String chromosome = vcfRecord.chrom();
    String[] alts = vcfRecord.alt();
    List<Double> altAfAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdShortVariantAnnotation gnomAdAnnotation =
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
        GnomAdShortVariantAnnotation.VariantData variantData = gnomAdAnnotation.getJoint();
        if (variantData == null) variantData = gnomAdAnnotation.getGenomes();
        if (variantData == null) variantData = gnomAdAnnotation.getExomes();

        altAnnotation = variantData.getAf();
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
