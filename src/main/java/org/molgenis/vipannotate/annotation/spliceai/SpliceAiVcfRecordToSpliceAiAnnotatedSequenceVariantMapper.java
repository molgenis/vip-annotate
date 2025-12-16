package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
import org.molgenis.vipannotate.format.vcf.AltAllele;
import org.molgenis.vipannotate.format.vcf.Ref;
import org.molgenis.vipannotate.util.AlleleUtils;
import org.molgenis.vipannotate.util.HgncToNcbiGeneIdMapper;

@RequiredArgsConstructor
public class SpliceAiVcfRecordToSpliceAiAnnotatedSequenceVariantMapper {
  private final ContigRegistry contigRegistry;
  private final HgncToNcbiGeneIdMapper geneIdMapper;

  public @Nullable SpliceAiAnnotatedSequenceVariant annotate(SpliceAiVcfRecord spliceAiVcfRecord) {
    SequenceVariant variant = createVariant(spliceAiVcfRecord);
    if (variant == null) return null;
    SpliceAiAnnotation annotation = createAnnotation(spliceAiVcfRecord);
    if (annotation == null) return null;
    return new SpliceAiAnnotatedSequenceVariant(variant, annotation);
  }

  @Nullable
  private SequenceVariant createVariant(SpliceAiVcfRecord spliceAiVcfRecord) {
    String chromStr = "chr" + spliceAiVcfRecord.chr().getIdentifier();

    Contig contig = contigRegistry.getContig(chromStr);
    if (contig == null) {
      return null; // skip contigs such as chr1_KI270766v1_alt
    }

    Ref ref = spliceAiVcfRecord.ref();
    AltAllele alt = spliceAiVcfRecord.alt();
    if (!AlleleUtils.isActg(alt.get())) {
      return null; // TODO support variants with 'N'
    }
    int start = spliceAiVcfRecord.pos().get();
    int refLength = ref.getBaseCount();
    int end = start + refLength - 1;
    SequenceVariantType type = SequenceVariantTypeDetector.determineType(refLength, alt);
    return new SequenceVariant(contig, start, end, alt, type);
  }

  @Nullable
  private SpliceAiAnnotation createAnnotation(SpliceAiVcfRecord spliceAiVcfRecord) {
    String hgncGeneSymbol = spliceAiVcfRecord.hgncGeneSymbol().toString();
    Integer ncbiGeneId = geneIdMapper.map(hgncGeneSymbol);
    if (ncbiGeneId == null) {
      return null;
    }

    double deltaScoreAcceptorGain = spliceAiVcfRecord.deltaScoreAcceptorGain();
    double deltaScoreAcceptorLoss = spliceAiVcfRecord.deltaScoreAcceptorLoss();
    double deltaScoreDonorGain = spliceAiVcfRecord.deltaScoreDonorGain();
    double deltaScoreDonorLoss = spliceAiVcfRecord.deltaScoreDonorLoss();

    // variant delta scores range from 0 to 1 and can be interpreted as the probability of the
    // variant being splice-altering, so it does not make sense to annotate delta positions when the
    // corresponding delta score is 0.
    Byte deltaPositionAcceptorGain =
        deltaScoreAcceptorGain != 0 ? spliceAiVcfRecord.deltaPositionAcceptorGain() : null;
    Byte deltaPositionAcceptorLoss =
        deltaScoreAcceptorLoss != 0 ? spliceAiVcfRecord.deltaPositionAcceptorLoss() : null;
    Byte deltaPositionDonorGain =
        deltaScoreDonorGain != 0 ? spliceAiVcfRecord.deltaPositionDonorGain() : null;
    Byte deltaPositionDonorLoss =
        deltaScoreDonorLoss != 0 ? spliceAiVcfRecord.deltaPositionDonorLoss() : null;

    return new SpliceAiAnnotation(
        ncbiGeneId,
        deltaScoreAcceptorGain,
        deltaScoreAcceptorLoss,
        deltaScoreDonorGain,
        deltaScoreDonorLoss,
        deltaPositionAcceptorGain,
        deltaPositionAcceptorLoss,
        deltaPositionDonorGain,
        deltaPositionDonorLoss);
  }
}
