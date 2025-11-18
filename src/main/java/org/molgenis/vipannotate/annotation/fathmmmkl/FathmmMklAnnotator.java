package org.molgenis.vipannotate.annotation.fathmmmkl;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class FathmmMklAnnotator extends BaseVcfRecordAnnotator<FathmmMklAnnotation> {
  private static final String INFO_ID_FATHMM_MKL = "FATHMM_MKL";

  private final SequenceVariantAnnotationDb<SequenceVariant, FathmmMklAnnotation> annotationDb;
  private boolean isNewAnnotation;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    isNewAnnotation =
        vcfMetaInfo.addOrUpdateInfo(
            INFO_ID_FATHMM_MKL, "A", "String", "FATHMM-MKL score", App.getName(), App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.getChrom().getIdentifier().toString());
    int start = vcfRecord.getPos().get();
    int stop = vcfRecord.getPos().get() + vcfRecord.getRef().getBaseCount() - 1;
    Alt alt = vcfRecord.getAlt();
    List<AltAllele> altAlleles = alt.getAlleles();

    VcfInfoSubfieldValueBuilder infoSubfieldBuilder = getVcfInfoSubfieldBuilder();
    for (AltAllele altAllele : altAlleles) {
      List<FathmmMklAnnotation> altAnnotations = getAltAnnotationList();
      annotationDb.findAnnotations(
          new SequenceVariant(
              contig,
              start,
              stop,
              altAllele,
              SequenceVariantTypeDetector.determineType(
                  vcfRecord.getRef().getBaseCount(), altAllele)),
          altAnnotations);
      appendAltAnnotations(altAnnotations, infoSubfieldBuilder);
    }

    writeInfoSubField(vcfRecord, INFO_ID_FATHMM_MKL, infoSubfieldBuilder, isNewAnnotation);
  }

  private static void appendAltAnnotations(
      List<FathmmMklAnnotation> altAnnotations, VcfInfoSubfieldValueBuilder infoSubFieldBuilder) {
    if (altAnnotations.isEmpty()) {
      infoSubFieldBuilder.appendValueMissing();
      return;
    }

    // FIXME remove workaround after fathmm annotation resource fix
    // only zero or one annotation per alt allowed
    //
    // fathmm annotation resource can contain multiple chr-pos-ref_len-alt annotations
    // example
    //          21      9688001 9688002 G       A       0.90720 A       0.21856 AEFI
    //          21      9688001 9688002 G       C       0.91092 A       0.26050 AEFI
    //          21      9688001 9688002 G       T       0.90949 A       0.24724 AEFI
    FathmmMklAnnotation altAnnotation = altAnnotations.getFirst();
    infoSubFieldBuilder.appendValue(altAnnotation.score(), 3);
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
