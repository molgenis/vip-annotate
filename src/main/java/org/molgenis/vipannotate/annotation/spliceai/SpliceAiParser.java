package org.molgenis.vipannotate.annotation.spliceai;

import org.molgenis.vipannotate.format.vcf.VcfRecord;

public class SpliceAiParser {
  public SpliceAiVcfRecord parse(VcfRecord spliceAiVcfRecord) {
    String[] tokens = spliceAiVcfRecord.info().get("SpliceAI").split("\\|", -1);

    String hgncGeneSymbol = tokens[1];
    float deltaScoreAcceptorGain = Float.parseFloat(tokens[2]);
    float deltaScoreAcceptorLoss = Float.parseFloat(tokens[3]);
    float deltaScoreDonorGain = Float.parseFloat(tokens[4]);
    float deltaScoreDonorLoss = Float.parseFloat(tokens[5]);
    byte deltaPositionAcceptorGain = Byte.parseByte(tokens[6]);
    byte deltaPositionAcceptorLoss = Byte.parseByte(tokens[7]);
    byte deltaPositionDonorGain = Byte.parseByte(tokens[8]);
    byte deltaPositionDonorLoss = Byte.parseByte(tokens[9]);

    return new SpliceAiVcfRecord(
        spliceAiVcfRecord.chrom(),
        spliceAiVcfRecord.pos(),
        spliceAiVcfRecord.ref(),
        spliceAiVcfRecord.alt()[0],
        hgncGeneSymbol,
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
