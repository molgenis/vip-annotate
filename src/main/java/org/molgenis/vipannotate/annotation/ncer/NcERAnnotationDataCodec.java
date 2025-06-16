package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.util.Encoder;

public class NcERAnnotationDataCodec {
  private static final int PERC_MIN = 0;
  private static final int PERC_MAX = 100;

  public short encode(Double score) {
    return Encoder.encodeDoubleAsShort(score, PERC_MIN, PERC_MAX);
  }

  public Double decode(short encodedScore) {
    return Encoder.decodeDoubleFromShort(encodedScore, PERC_MIN, PERC_MAX);
  }
}
