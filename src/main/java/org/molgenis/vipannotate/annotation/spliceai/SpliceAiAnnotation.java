package org.molgenis.vipannotate.annotation.spliceai;

import static org.molgenis.vipannotate.util.Numbers.*;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.annotation.Annotation;

public record SpliceAiAnnotation(
    int ncbiGeneId,
    double deltaScoreAcceptorGain,
    double deltaScoreAcceptorLoss,
    double deltaScoreDonorGain,
    double deltaScoreDonorLoss,
    @Nullable Byte deltaPositionAcceptorGain,
    @Nullable Byte deltaPositionAcceptorLoss,
    @Nullable Byte deltaPositionDonorGain,
    @Nullable Byte deltaPositionDonorLoss)
    implements Annotation {

  public SpliceAiAnnotation {
    validatePositive(ncbiGeneId);
    validateUnitInterval(deltaScoreAcceptorGain);
    validateUnitInterval(deltaScoreAcceptorLoss);
    validateUnitInterval(deltaScoreDonorGain);
    validateUnitInterval(deltaScoreDonorLoss);
    validateIntervalOrNull(deltaPositionAcceptorGain, (byte) -50, (byte) 50);
    validateIntervalOrNull(deltaPositionAcceptorLoss, (byte) -50, (byte) 50);
    validateIntervalOrNull(deltaPositionDonorGain, (byte) -50, (byte) 50);
    validateIntervalOrNull(deltaPositionDonorLoss, (byte) -50, (byte) 50);
  }
}
