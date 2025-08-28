package org.molgenis.vipannotate.annotation.spliceai;

import org.molgenis.vipannotate.annotation.Annotation;

public record SpliceAiAnnotation(
    int ncbiGeneId,
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
