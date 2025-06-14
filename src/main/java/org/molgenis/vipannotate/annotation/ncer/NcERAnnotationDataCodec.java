package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.util.Quantized16UnitIntervalDouble;

public class NcERAnnotationDataCodec {
  public static final int NR_ANNOTATION_BYTES = 2;

  // FIXME remove static to enable upstream unit testing
  public static short encodeScore(double score) {
    if (score < 0 || score > 100) {
      throw new IllegalArgumentException("score '%f' must be in range [0, 100]".formatted(score));
    }
    double percUnitInterval = score / 100;
    return Quantized16UnitIntervalDouble.toShort(percUnitInterval);
  }

  public Double decodeScore(short encodedScore) {
    Double encodedDouble = Quantized16UnitIntervalDouble.toDouble(encodedScore);
    return encodedDouble != null ? encodedDouble * 100 : null;
  }
}
