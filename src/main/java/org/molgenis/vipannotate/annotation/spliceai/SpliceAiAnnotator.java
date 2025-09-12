package org.molgenis.vipannotate.annotation.spliceai;

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
public class SpliceAiAnnotator implements VcfRecordAnnotator {
  private static final String INFO_ID_SPLICEAI = "SpliceAI";

  private final SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> snvAnnotationDb;
  private final SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> indelAnnotationDb;
  private final VcfRecordAnnotationWriter vcfRecordAnnotationWriter;
  private @Nullable StringBuilder reusableStringBuilder;
  private @Nullable DecimalFormat reusableDecimalFormat;

  @Override
  public void updateHeader(VcfHeader vcfHeader) {
    VcfMetaInfo vcfMetaInfo = vcfHeader.vcfMetaInfo();

    vcfMetaInfo.addOrUpdateInfo(
        INFO_ID_SPLICEAI,
        "A",
        "String",
        "SpliceAI annotations per ALT allele. Multiple annotations per allele are separated by '&'. Each annotation is formatted as 'NCBI_GENE_ID|DS_AG|DS_AL|DS_DG|DS_DL|DP_AG|DP_AL|DP_DG|DP_DL' where DS stands for delta scores, DP stands for delta positions, AG for acceptor gain, AL for acceptor loss, DG for donor gain and DL for donor loss.",
        App.getName(),
        App.getVersion());
  }

  @Override
  public void annotate(VcfRecord vcfRecord) {
    Contig contig = new Contig(vcfRecord.chrom());
    int start = vcfRecord.pos();
    int stop = vcfRecord.pos() + vcfRecord.ref().length() - 1;
    @Nullable String[] alts = vcfRecord.alt();

    List<List<SpliceAiAnnotation>> altsAnnotations = new ArrayList<>(alts.length);
    for (String alt : alts) {
      SequenceVariantType sequenceVariantType =
          SequenceVariant.fromVcfString(vcfRecord.ref().length(), alt);
      SequenceVariantAnnotationDb<SequenceVariant, SpliceAiAnnotation> annotationDb =
          sequenceVariantType == SequenceVariantType.SNV ? snvAnnotationDb : indelAnnotationDb;
      List<SpliceAiAnnotation> altAnnotations =
          annotationDb.findAnnotations(
              new SequenceVariant(
                  contig, start, stop, AltAlleleRegistry.get(alt), sequenceVariantType));
      altsAnnotations.add(altAnnotations);
    }

    vcfRecordAnnotationWriter.writeInfoString(
        vcfRecord, altsAnnotations, INFO_ID_SPLICEAI, this::createInfoAltString);
  }

  private @Nullable CharSequence createInfoAltString(List<SpliceAiAnnotation> annotations) {
    if (reusableStringBuilder == null) {
      reusableStringBuilder = new StringBuilder();
    } else {
      reusableStringBuilder.setLength(0);
    }

    if (reusableDecimalFormat == null) {
      reusableDecimalFormat = DecimalFormatRegistry.getDecimalFormat("#.##");
    }

    if (!annotations.isEmpty()) {
      for (int i = 0, annotationsSize = annotations.size(); i < annotationsSize; i++) {
        if (i > 0) {
          reusableStringBuilder.append('&');
        }

        SpliceAiAnnotation annotation = annotations.get(i);
        reusableStringBuilder
            .append(annotation.ncbiGeneId())
            .append('|')
            .append(reusableDecimalFormat.format(annotation.deltaScoreAcceptorGain()))
            .append('|')
            .append(reusableDecimalFormat.format(annotation.deltaScoreAcceptorLoss()))
            .append('|')
            .append(reusableDecimalFormat.format(annotation.deltaScoreDonorGain()))
            .append('|')
            .append(reusableDecimalFormat.format(annotation.deltaScoreDonorLoss()))
            .append('|');

        Byte deltaPositionAcceptorGain = annotation.deltaPositionAcceptorGain();
        if (deltaPositionAcceptorGain != null) {
          reusableStringBuilder.append(deltaPositionAcceptorGain);
        }

        reusableStringBuilder.append('|');
        Byte deltaPositionAcceptorLoss = annotation.deltaPositionAcceptorLoss();
        if (deltaPositionAcceptorLoss != null) {
          reusableStringBuilder.append(deltaPositionAcceptorLoss);
        }

        reusableStringBuilder.append('|');
        Byte deltaPositionDonorGain = annotation.deltaPositionDonorGain();
        if (deltaPositionDonorGain != null) {
          reusableStringBuilder.append(deltaPositionDonorGain);
        }

        reusableStringBuilder.append('|');
        Byte deltaPositionDonorLoss = annotation.deltaPositionDonorLoss();
        if (deltaPositionDonorLoss != null) {
          reusableStringBuilder.append(deltaPositionDonorLoss);
        }
      }
    } else {
      reusableStringBuilder.append('.');
    }
    return reusableStringBuilder;
  }

  @Override
  public void close() {
    snvAnnotationDb.close();
    indelAnnotationDb.close();
  }
}
