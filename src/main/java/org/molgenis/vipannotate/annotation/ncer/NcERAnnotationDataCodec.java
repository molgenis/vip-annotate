package org.molgenis.vipannotate.annotation.ncer;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.DoubleCodec;

public class NcERAnnotationDataCodec {
  static final int PERC_MIN = 0;
  static final int PERC_MAX = 100;

  private final DoubleCodec doubleCodec;

  public NcERAnnotationDataCodec() {
    this(new DoubleCodec());
  }

  NcERAnnotationDataCodec(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public @Nullable Double decode(short encodedScore) {
    return doubleCodec.decodeDoubleFromShort(encodedScore, PERC_MIN, PERC_MAX);
  }
}
