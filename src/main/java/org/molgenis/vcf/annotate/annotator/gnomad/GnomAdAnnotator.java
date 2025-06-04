package org.molgenis.vcf.annotate.annotator.gnomad;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.db.effect.model.FuryFactory;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.db.exact.format.AnnotationDbImpl;
import org.molgenis.vcf.annotate.db.gnomad.GnomAdShortVariantAnnotation;
import org.molgenis.vcf.annotate.util.ContigUtils;
import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;

@RequiredArgsConstructor
public class GnomAdAnnotator implements VcfRecordAnnotator {
  @NonNull private final AnnotationDbImpl<GnomAdShortVariantAnnotation> annotationDb;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader.addLine(
        "##INFO=<ID=" + "gnomAD_AF" + ",Number=A,Type=Float,Description=\"gnomAD AF\">");
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    FuryFactory.Chromosome chromosome = ContigUtils.map(vcfRecord.getChrom());
    if (chromosome == null) {
      return;
    }

    String[] alts = vcfRecord.getAlts();
    List<Double> altAfAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdShortVariantAnnotation gnomAdAnnotation =
          annotationDb.findAnnotations(
              new Variant(
                  chromosome.getId(),
                  vcfRecord.getPos(),
                  vcfRecord.getPos() + vcfRecord.getRef().length(),
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
          builder.append(altAnnotation);
        } else {
          builder.append('.');
        }
      }
      vcfRecord.addInfo(builder.toString());
    }
  }

  @Override
  public void close() throws Exception {
    annotationDb.close();
  }
}
