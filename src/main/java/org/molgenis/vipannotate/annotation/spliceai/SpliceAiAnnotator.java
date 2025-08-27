package org.molgenis.vipannotate.annotation.spliceai;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.App;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.VcfHeader;
import org.molgenis.vipannotate.format.vcf.VcfMetaInfo;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

@RequiredArgsConstructor
public class SpliceAiAnnotator implements VcfRecordAnnotator {
  private static final String INFO_ID_SPLICEAI_DSAG = "spliceAI_DSAG";
  private static final String INFO_ID_SPLICEAI_DSAL = "spliceAI_DSAL";
  private static final String INFO_ID_SPLICEAI_DSDG = "spliceAI_DSDG";
  private static final String INFO_ID_SPLICEAI_DSDL = "spliceAI_DSDL";
  private static final String INFO_ID_SPLICEAI_DPAG = "spliceAI_DPAG";
  private static final String INFO_ID_SPLICEAI_DPAL = "spliceAI_DPAL";
  private static final String INFO_ID_SPLICEAI_DPDG = "spliceAI_DPDG";
  private static final String INFO_ID_SPLICEAI_DPDL = "spliceAI_DPDL";

  private final SequenceVariantAnnotationDb<SequenceVariantGeneContext, SpliceAiAnnotation>
      snvAnnotationDb;
  private final SequenceVariantAnnotationDb<SequenceVariantGeneContext, SpliceAiAnnotation>
      indelAnnotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DSAG,
        "A",
        "Float",
        "SpliceAI delta score for acceptor gain",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DSAL,
        "A",
        "Float",
        "SpliceAI delta score for acceptor loss",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DSDG,
        "A",
        "Float",
        "SpliceAI delta score for donor gain",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DSDL,
        "A",
        "Float",
        "SpliceAI delta score for donor loss",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DPAG,
        "A",
        "Integer",
        "SpliceAI delta position for acceptor gain",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DPAL,
        "A",
        "Integer",
        "SpliceAI delta position for acceptor loss",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DPDG,
        "A",
        "Integer",
        "SpliceAI delta position for donor gain",
        App.getName(),
        App.getVersion());

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI_DPDL,
        "A",
        "Integer",
        "SpliceAI delta position for donor loss",
        App.getName(),
        App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    @Nullable String[] alts = vcfRecord.alt();

    List<@Nullable SpliceAiAnnotation> altAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      SequenceVariantType sequenceVariantType =
          SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt);
      SequenceVariantAnnotationDb<SequenceVariantGeneContext, SpliceAiAnnotation> annotationDb =
          sequenceVariantType == SequenceVariantType.SNV ? snvAnnotationDb : indelAnnotationDb;
      Collection<SpliceAiAnnotation> altAnnotation =
          annotationDb.findAnnotations(
              new SequenceVariantGeneContext(
                  contig,
                  start,
                  stop,
                  AltAlleleRegistry.get(alt),
                  sequenceVariantType,
                  new Gene(Gene.Source.NCBI, -1))); // FIXME actual gene
      altAnnotations.addAll(altAnnotation);
    }

    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DSAG,
        SpliceAiAnnotation::deltaScoreAcceptorGain,
        "#.##");
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DSAL,
        SpliceAiAnnotation::deltaScoreAcceptorLoss,
        "#.##");
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DSDG,
        SpliceAiAnnotation::deltaScoreDonorGain,
        "#.##");
    vcfRecordAnnotationWriter.writeInfoDouble(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DSDL,
        SpliceAiAnnotation::deltaScoreDonorLoss,
        "#.##");
    vcfRecordAnnotationWriter.writeInfoInteger(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DPAG,
        (annotation) -> (int) annotation.deltaPositionAcceptorGain());
    vcfRecordAnnotationWriter.writeInfoInteger(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DPAL,
        (annotation) -> (int) annotation.deltaPositionAcceptorLoss());
    vcfRecordAnnotationWriter.writeInfoInteger(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DPDG,
        (annotation) -> (int) annotation.deltaPositionDonorGain());
    vcfRecordAnnotationWriter.writeInfoInteger(
        vcfRecord,
        altAnnotations,
        INFO_ID_SPLICEAI_DPDL,
        (annotation) -> (int) annotation.deltaPositionDonorLoss());
  }

  @Override
  public void close() {
    snvAnnotationDb.close();
    indelAnnotationDb.close();
  }
}
