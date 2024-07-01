package org.molgenis.vipannotate.annotator.ncer;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vipannotate.annotator.VcfRecordAnnotator;
import org.molgenis.vipannotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vipannotate.db.exact.Variant;
import org.molgenis.vipannotate.vcf.VcfHeader;
import org.molgenis.vipannotate.vcf.VcfRecord;

public class NcERAnnotator implements VcfRecordAnnotator {
  @NonNull private final ContigPosAnnotationDb annotationDb;
  private final DecimalFormat decimalFormat;

  public NcERAnnotator(@NonNull ContigPosAnnotationDb annotationDb) {
    this.annotationDb = annotationDb;
    this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    this.decimalFormat.applyPattern("##.####");
  }

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    vcfHeader.addLine(
        "##INFO=<ID="
            + NcERAnnotationDecoder.ANNOTATION_ID
            + ",Number=A,Type=Float,Description=\"ncER score\">");
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    String[] alts = vcfRecord.getAlts();
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
                  vcfRecord.getChrom(),
                  vcfRecord.getPos(),
                  vcfRecord.getPos() + vcfRecord.getRef().length() - 1,
                  alt.getBytes(StandardCharsets.UTF_8)));

      altAnnotations.add(altAnnotation);
    }

    if (altAnnotations.stream().anyMatch(Objects::nonNull)) {
      StringBuilder builder = new StringBuilder();
      builder.append(NcERAnnotationDecoder.ANNOTATION_ID).append('=');
      for (Double altAnnotation : altAnnotations) {
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
