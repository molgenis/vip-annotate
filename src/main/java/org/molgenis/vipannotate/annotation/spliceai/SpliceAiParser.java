package org.molgenis.vipannotate.annotation.spliceai;

import static org.molgenis.vipannotate.util.Numbers.safeIntToByte;

import org.molgenis.vipannotate.format.vcf.StringView;
import org.molgenis.vipannotate.format.vcf.VcfRecord;

public class SpliceAiParser {
  public SpliceAiVcfRecord parse(VcfRecord spliceAiVcfRecord) {
    // perf: this assumes vcf info format is SpliceAI=<CharSequence>
    CharSequence cs = spliceAiVcfRecord.getInfo().getRaw("SpliceAI=".length());

    // get info field token positions
    int p1 = StringView.indexOf(cs, '|');
    int p2 = StringView.indexOf(cs, '|', p1 + 1);
    int p3 = StringView.indexOf(cs, '|', p2 + 1);
    int p4 = StringView.indexOf(cs, '|', p3 + 1);
    int p5 = StringView.indexOf(cs, '|', p4 + 1);
    int p6 = StringView.indexOf(cs, '|', p5 + 1);
    int p7 = StringView.indexOf(cs, '|', p6 + 1);
    int p8 = StringView.indexOf(cs, '|', p7 + 1);
    int p9 = StringView.indexOf(cs, '|', p8 + 1);

    // parse info field tokens
    StringView hgncGeneSymbol = new StringView(cs, p1 + 1, p2);

    // TODO perf: introduce and use float parser that works on CharSequence
    // e.g. https://github.com/wrandelshofer/FastDoubleParser
    float deltaScoreAcceptorGain = Float.parseFloat(cs.subSequence(p2 + 1, p3).toString());
    float deltaScoreAcceptorLoss = Float.parseFloat(cs.subSequence(p3 + 1, p4).toString());
    float deltaScoreDonorGain = Float.parseFloat(cs.subSequence(p4 + 1, p5).toString());
    float deltaScoreDonorLoss = Float.parseFloat(cs.subSequence(p5 + 1, p6).toString());

    byte deltaPositionAcceptorGain = parseByte(cs, p6 + 1, p7);
    byte deltaPositionAcceptorLoss = parseByte(cs, p7 + 1, p8);
    byte deltaPositionDonorGain = parseByte(cs, p8 + 1, p9);
    byte deltaPositionDonorLoss = parseByte(cs, p9 + 1, cs.length());

    return new SpliceAiVcfRecord(
        spliceAiVcfRecord.getChrom(),
        spliceAiVcfRecord.getPos(),
        spliceAiVcfRecord.getRef(),
        spliceAiVcfRecord.getAlt().getFirstAllele(),
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

  private static byte parseByte(CharSequence charSequence, int beginIndex, int endIndex) {
    return safeIntToByte(Integer.parseInt(charSequence, beginIndex, endIndex, 10));
  }
}
