package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.util.Encoder;

public class NcERAnnotationDataCodec {
  static final int PERC_MIN = 0;
  static final int PERC_MAX = 100;

  public Double decode(short encodedScore) {
    return Encoder.decodeDoubleFromShort(encodedScore, PERC_MIN, PERC_MAX);
  }
}
