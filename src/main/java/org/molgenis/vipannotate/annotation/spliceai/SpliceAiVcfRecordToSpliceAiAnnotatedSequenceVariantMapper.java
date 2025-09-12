package org.molgenis.vipannotate.annotation.spliceai;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.*;
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

  private @Nullable SequenceVariant createVariant(SpliceAiVcfRecord spliceAiVcfRecord) {
    String chromStr = "chr" + spliceAiVcfRecord.chr();

    Contig contig = contigRegistry.getContig(chromStr);
    if (contig == null) {
      throw new RuntimeException("Contig " + chromStr + " not found");
    }

    String ref = spliceAiVcfRecord.ref();
    String alt = spliceAiVcfRecord.alt();
    if (alt.indexOf('N') != -1) {
      return null; // FIXME see VariantEncoder.encodeAltBase
    }
    int start = spliceAiVcfRecord.pos();
    int end = start + ref.length() - 1;
    AltAllele altAllele = AltAlleleRegistry.get(alt);
    SequenceVariantType type = SequenceVariant.fromVcfString(ref.length(), alt);
    return new SequenceVariant(contig, start, end, altAllele, type);
  }

  private @Nullable SpliceAiAnnotation createAnnotation(SpliceAiVcfRecord spliceAiVcfRecord) {
    String hgncGeneSymbol = spliceAiVcfRecord.hgncGeneSymbol();
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
