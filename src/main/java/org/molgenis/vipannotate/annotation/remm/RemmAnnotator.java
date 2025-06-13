package org.molgenis.vipannotate.annotation.remm;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.ContigPosAnnotationDb;
import org.molgenis.vipannotate.annotation.Variant;
import org.molgenis.vipannotate.annotation.VcfRecordAnnotator;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

public class RemmAnnotator implements VcfRecordAnnotator {
  private final ContigPosAnnotationDb annotationDb;
  private final DecimalFormat decimalFormat;

  public RemmAnnotator(@NonNull ContigPosAnnotationDb annotationDb) {
    this.annotationDb = annotationDb;
    this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    this.decimalFormat.applyPattern("#.###");
  }

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader
        .vcfMetaInfo()
        .addOrUpdateInfo(
            RemmAnnotationDecoder.ANNOTATION_ID,
            "A",
            "Float",
            "REMM score",
            App.getName(),
            App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    String[] alts = vcfRecord.alt();
    List<Double> altAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      // FIXME handle all alt cases
      // Each allele in this list must be one of: a non-empty String of bases (A,C,G,T,N;case
      // insensitive); the ‘*’ symbol (allele missing due to overlapping deletion); the MISSING
      // value ‘.’ (no variant);an angle-bracketed ID String (“<ID>”); the unspecified allele “<*>”
      // as described in Section 5.5; or a breakend replacement string as described in Section 5.4
      Double altAnnotation =
          annotationDb.findAnnotations(
              new Variant(
                  vcfRecord.chrom(),
                  Math.toIntExact(vcfRecord.pos()), // FIXME annotationDb should accept long?
                  Math.toIntExact(
                      vcfRecord.pos()
                          + vcfRecord.ref().length()
                          - 1), // FIXME annotationDb should accept long?
                  alt.getBytes(StandardCharsets.UTF_8)));

      altAnnotations.add(altAnnotation);
    }

    if (altAnnotations.stream().anyMatch(Objects::nonNull)) {
      StringBuilder builder = new StringBuilder();
      for (Double altAnnotation : altAnnotations) {
        if (altAnnotation != null) {
          builder.append(decimalFormat.format(altAnnotation));
        } else {
          builder.append('.');
        }
      }
      vcfRecord.info().put(RemmAnnotationDecoder.ANNOTATION_ID, builder.toString());
    } else {
      vcfRecord.info().remove(RemmAnnotationDecoder.ANNOTATION_ID);
    }
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
