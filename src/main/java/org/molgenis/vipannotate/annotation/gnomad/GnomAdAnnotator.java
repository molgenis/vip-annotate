package org.molgenis.vipannotate.annotation.gnomad;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.Contig;
import org.molgenis.vipannotate.annotation.GenomeSequenceVariantAnnotationDb;
import org.molgenis.vipannotate.annotation.SequenceVariant;
import org.molgenis.vipannotate.annotation.VcfRecordAnnotator;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public class GnomAdAnnotator implements VcfRecordAnnotator {
  private final GenomeSequenceVariantAnnotationDb<GnomAdAnnotation> annotationDb;
  private final DecimalFormat decimalFormat;

  public GnomAdAnnotator(
      @NonNull GenomeSequenceVariantAnnotationDb<GnomAdAnnotation> annotationDb) {
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
    Contig chromosome = new Contig(vcfRecord.chrom());
    String[] alts = vcfRecord.alt();
    List<Double> altAfAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      GnomAdAnnotation gnomAdAnnotation =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  chromosome,
                  vcfRecord.pos(),
                  vcfRecord.pos() + vcfRecord.ref().length() - 1,
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
