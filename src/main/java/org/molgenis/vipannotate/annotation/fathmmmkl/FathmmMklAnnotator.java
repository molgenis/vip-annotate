package org.molgenis.vipannotate.annotation.fathmmmkl;

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
public class FathmmMklAnnotator implements VcfRecordAnnotator {
  private static final String INFO_ID_FATHMM_MKL = "FATHMM_MKL";

  private final SequenceVariantAnnotationDb<SequenceVariant, FathmmMklAnnotation> annotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;
  private @Nullable StringBuilder reusableStringBuilder;
  private @Nullable DecimalFormat reusableDecimalFormat;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_FATHMM_MKL, "A", "String", "FATHMM-MKL score", App.getName(), App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    @Nullable String[] alts = vcfRecord.alt();

    List<@Nullable FathmmMklAnnotation> altsAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      List<FathmmMklAnnotation> altAnnotations =
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
        // fathmm annotation resource can contain multiple chr-pos-ref_len-alt annotations
        // FIXME remove workaround after fathmm annotation resource fix
        altsAnnotations.add(altAnnotations.getFirst());
      }
    }

    vcfRecordAnnotationWriter.writeInfoString(
        vcfRecord, altsAnnotations, INFO_ID_FATHMM_MKL, this::createInfoAltString);
  }

  private @Nullable CharSequence createInfoAltString(@Nullable FathmmMklAnnotation annotation) {
    if (reusableStringBuilder == null) {
      reusableStringBuilder = new StringBuilder();
    } else {
      reusableStringBuilder.setLength(0);
    }

    if (reusableDecimalFormat == null) {
      reusableDecimalFormat = DecimalFormatRegistry.getDecimalFormat("#.###");
    }

    if (annotation != null) {
      reusableStringBuilder.append(reusableDecimalFormat.format(annotation.score()));
    } else {
      reusableStringBuilder.append('.');
    }

    return reusableStringBuilder;
  }

  @Override
  public void close() {
    annotationDb.close();
  }
}
