package org.molgenis.vcf.annotate.db.chrpos.ncer;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.molgenis.vcf.annotate.annotator.VcfRecordAnnotator;
import org.molgenis.vcf.annotate.db.chrpos.ContigPosAnnotationDb;
import org.molgenis.vcf.annotate.db.effect.model.FuryFactory;
import org.molgenis.vcf.annotate.db.exact.Variant;
import org.molgenis.vcf.annotate.util.ContigUtils;
import org.molgenis.vcf.annotate.util.MappableZipFile;
import org.molgenis.vcf.annotate.vcf.VcfHeader;
import org.molgenis.vcf.annotate.vcf.VcfRecord;

public class VcfRecordAnnotatorNcER implements VcfRecordAnnotator {
  @NonNull private final ContigPosAnnotationDb annotationDb;
  private final DecimalFormat decimalFormat;

  public VcfRecordAnnotatorNcER(@NonNull ContigPosAnnotationDb annotationDb) {
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
    FuryFactory.Chromosome chromosome = ContigUtils.map(vcfRecord.getChrom());
    if (chromosome == null) {
      return;
    }

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
                  chromosome.getId(),
                  vcfRecord.getPos(),
                  vcfRecord.getPos() + vcfRecord.getRef().length(),
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

  public static VcfRecordAnnotatorNcER create(MappableZipFile zipFile) {
    ContigPosAnnotationDb contigPosAnnotationDb =
        new ContigPosAnnotationDb(
            zipFile,
            new NcERAnnotationDecoder(),
            NcERAnnotationDecoder.NR_ANNOTATION_BYTES,
            NcERAnnotationDecoder.ANNOTATION_ID);
    return new VcfRecordAnnotatorNcER(contigPosAnnotationDb);
  }

  @Override
  public void close() throws Exception {
    annotationDb.close();
  }
}
