package org.molgenis.vipannotate.annotation.spliceai;

import org.molgenis.vipannotate.format.vcf.*;

public record SpliceAiVcfRecord(
    Chrom chr, // hg38 contig (so without the chr prefix)
    Pos pos, // 1-based
    Ref ref,
    AltAllele alt,
    StringView hgncGeneSymbol,
    double deltaScoreAcceptorGain,
    double deltaScoreAcceptorLoss,
    double deltaScoreDonorGain,
    double deltaScoreDonorLoss,
    byte deltaPositionAcceptorGain,
    byte deltaPositionAcceptorLoss,
    byte deltaPositionDonorGain,
    byte deltaPositionDonorLoss) {}
