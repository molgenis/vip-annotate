package org.molgenis.vipannotate.annotation.spliceai;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.molgenis.vipannotate.AppMetadata;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.*;
import org.molgenis.vipannotate.util.ClosableUtils;

@RequiredArgsConstructor
public class SpliceAiAnnotator extends BaseVcfRecordAnnotator<SpliceAiAnnotation> {
  private static final String INFO_ID_SPLICEAI = "SpliceAI";

  private final SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> annotationDb;
  private boolean isNewAnnotation;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    isNewAnnotation =
        vcfMetaInfo.addOrUpdateInfo(
            INFO_ID_SPLICEAI,
            "A",
            "String",
            "SpliceAI annotations per ALT allele. Multiple annotations per allele are separated by '&'. Each annotation is formatted as 'NCBI_GENE_ID|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL' where DS stands for delta scores, DP stands for delta positions, AG for acceptor gain, AL for acceptor loss, DG for donor gain and DL for donor loss.",
            AppMetadata.getName(),
            AppMetadata.getVersion());
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
      SequenceVariantType sequenceVariantType =
          SequenceVariantTypeDetector.determineType(vcfRecord.getRef().getBaseCount(), altAllele);
      List<SpliceAiAnnotation> altAnnotations = getAltAnnotationList();
      annotationDb.findAnnotations(
          new SequenceVariant(contig, start, stop, altAllele, sequenceVariantType), altAnnotations);

      appendAltAnnotations(altAnnotations, infoSubfieldBuilder);
    }

    writeInfoSubField(vcfRecord, INFO_ID_SPLICEAI, infoSubfieldBuilder, isNewAnnotation);
  }

  private void appendAltAnnotations(
      List<SpliceAiAnnotation> altAnnotations, VcfInfoSubfieldValueBuilder infoSubFieldBuilder) {
    if (altAnnotations.isEmpty()) {
      infoSubFieldBuilder.appendValueMissing();
      return;
    }

    infoSubFieldBuilder.startRawValue();
    for (int i = 0, annotationsSize = altAnnotations.size(); i < annotationsSize; i++) {
      if (i > 0) {
        infoSubFieldBuilder.appendRaw('&');
      }

      SpliceAiAnnotation annotation = altAnnotations.get(i);
      infoSubFieldBuilder.appendRaw(annotation.ncbiGeneId());
      infoSubFieldBuilder.appendRaw('|');
      infoSubFieldBuilder.appendRaw(annotation.deltaScoreAcceptorGain(), 2);
      infoSubFieldBuilder.appendRaw('|');
      infoSubFieldBuilder.appendRaw(annotation.deltaScoreAcceptorLoss(), 2);
      infoSubFieldBuilder.appendRaw('|');
      infoSubFieldBuilder.appendRaw(annotation.deltaScoreDonorGain(), 2);
      infoSubFieldBuilder.appendRaw('|');
      infoSubFieldBuilder.appendRaw(annotation.deltaScoreDonorLoss(), 2);
      infoSubFieldBuilder.appendRaw('|');

      Byte deltaPositionAcceptorGain = annotation.deltaPositionAcceptorGain();
      if (deltaPositionAcceptorGain != null) {
        infoSubFieldBuilder.appendRaw(deltaPositionAcceptorGain);
      }

      infoSubFieldBuilder.appendRaw('|');
      Byte deltaPositionAcceptorLoss = annotation.deltaPositionAcceptorLoss();
      if (deltaPositionAcceptorLoss != null) {
        infoSubFieldBuilder.appendRaw(deltaPositionAcceptorLoss);
      }

      infoSubFieldBuilder.appendRaw('|');
      Byte deltaPositionDonorGain = annotation.deltaPositionDonorGain();
      if (deltaPositionDonorGain != null) {
        infoSubFieldBuilder.appendRaw(deltaPositionDonorGain);
      }

      infoSubFieldBuilder.appendRaw('|');
      Byte deltaPositionDonorLoss = annotation.deltaPositionDonorLoss();
      if (deltaPositionDonorLoss != null) {
        infoSubFieldBuilder.appendRaw(deltaPositionDonorLoss);
      }
    }
    infoSubFieldBuilder.endRawValue();
  }

  @Override
  public void close() {
    ClosableUtils.close(annotationDb);
  }
}
