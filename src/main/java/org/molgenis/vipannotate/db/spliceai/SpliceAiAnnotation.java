package org.molgenis.vipannotate.db.spliceai;

import java.io.Serializable;
import lombok.*;

@Value
@EqualsAndHashCode
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SpliceAiAnnotation implements Serializable {
  // note: SpliceAI VEP plugin mentions that variant allele can contain multiple scores for
  // overlapping genes, but could not detect a case in the precomputed scores

  // add RefSeq gene id --> store as var int
  // position fits in 7 bits leaving 1 bit as a flag for something else
  // persisted scores are strings in range [0.00, 1.00] --> 101 possibilities
  // score fits in 7 bits leaving 1 bit as a flag for something else
  // data contains a lot of 0.00|0.00|0.00|0.00
  // format:
  // 1-5 bytes for gene identifier
  //   4 bytes for scores
  //   4 bytes for positions
  // multiplied by amount of records
  // instead of writing List<SpliceAiGeneAnnotation> write control bit (append to end of refseq gene
  // id?)
  float deltaScoreAcceptorGain;
  float deltaScoreAcceptorLoss;
  float deltaScoreDonorGain;
  float deltaScoreDonorLoss;
  byte deltaPositionAcceptorGain;
  byte deltaPositionAcceptorLoss;
  byte deltaPositionDonorGain;
  byte deltaPositionDonorLoss;
}
