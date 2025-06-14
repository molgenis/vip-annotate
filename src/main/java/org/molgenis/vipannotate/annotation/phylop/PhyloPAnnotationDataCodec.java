package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.util.Quantized8UnitIntervalDouble;

/** hg38.phyloP100way.bed.gz: min=-11.726 max=9.94 */
public class PhyloPAnnotationDataCodec {
  private static final double scoreMin = -11.726d;
  private static final double scoreMax = 9.94d;

  // FIXME remove static to enable upstream unit testing
  public static byte encode(String scoreStr) {
    Double scoreUnitInterval;
    if (scoreStr != null) {
      double score = Double.parseDouble(scoreStr);
      scoreUnitInterval = (score - scoreMin) / (scoreMax - scoreMin);
    } else {
      scoreUnitInterval = null;
    }
    return Quantized8UnitIntervalDouble.toByte(scoreUnitInterval);
  }

  public Double decode(byte encodedScore) {
    Double scoreUnitInterval = Quantized8UnitIntervalDouble.toDouble(encodedScore);
    return scoreUnitInterval != null
        ? (scoreUnitInterval * (scoreMax - scoreMin)) + scoreMin
        : null;
  }
}
