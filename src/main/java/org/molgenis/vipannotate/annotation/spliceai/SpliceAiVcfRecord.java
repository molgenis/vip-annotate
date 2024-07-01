package org.molgenis.vipannotate.annotation.spliceai;

public record SpliceAiVcfRecord(
    String chr, // hg38 contig (so without the chr prefix)
    int pos, // 1-based
    String ref,
    String alt,
    String hgncGeneSymbol,
    double deltaScoreAcceptorGain,
    double deltaScoreAcceptorLoss,
    double deltaScoreDonorGain,
    double deltaScoreDonorLoss,
    byte deltaPositionAcceptorGain,
    byte deltaPositionAcceptorLoss,
    byte deltaPositionDonorGain,
    byte deltaPositionDonorLoss) {}
