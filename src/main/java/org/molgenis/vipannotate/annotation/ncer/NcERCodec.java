package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.util.Quantized16UnitIntervalDouble;

public class NcERCodec {
  public static short encode(double perc) {
    if (perc < 0 || perc > 100) {
      throw new IllegalArgumentException("score '%f' must be in range [0, 100]".formatted(perc));
    }
    double percUnitInterval = perc / 100;
    return Quantized16UnitIntervalDouble.toShort(percUnitInterval);
  }

  public static Double decode(short encodedPerc) {
    Double encodedDouble = Quantized16UnitIntervalDouble.toDouble(encodedPerc);
    return encodedDouble != null ? encodedDouble * 100 : null;
  }
}
