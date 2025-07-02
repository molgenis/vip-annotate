package org.molgenis.vipannotate.annotation.spliceai;

import org.molgenis.vipannotate.annotation.Annotation;

public record SpliceAiAnnotation(
    // String hgncGeneSymbol, // FIXME add gene identifier instead of symbol
    double deltaScoreAcceptorGain,
    double deltaScoreAcceptorLoss,
    double deltaScoreDonorGain,
    double deltaScoreDonorLoss,
    byte deltaPositionAcceptorGain,
    byte deltaPositionAcceptorLoss,
    byte deltaPositionDonorGain,
    byte deltaPositionDonorLoss)
    implements Annotation {

  // TODO add constructor with parameter validation
}
