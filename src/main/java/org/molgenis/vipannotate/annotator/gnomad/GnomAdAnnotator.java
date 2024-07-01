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
    vcfHeader.addLine(
        "##INFO=<ID=" + "gnomAD_AF" + ",Number=A,Type=Float,Description=\"gnomAD AF\">");
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    String chromosome = vcfRecord.getChrom();
    String[] alts = vcfRecord.getAlts();
    List<Double> altAfAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdShortVariantAnnotation gnomAdAnnotation =
          annotationDb.findAnnotations(
              new Variant(
                  chromosome,
                  vcfRecord.getPos(),
                  vcfRecord.getPos() + vcfRecord.getRef().length() - 1,
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
      builder.append("gnomAD_AF").append('=');
      for (Double altAnnotation : altAfAnnotations) {
        if (altAnnotation != null) {
          builder.append(decimalFormat.format(altAnnotation));
        } else {
          builder.append('.');
        }
      }
      vcfRecord.addInfo(builder.toString());
    }
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
