package org.molgenis.vipannotate.annotation.phylop;

import org.jspecify.annotations.Nullable;
import org.molgenis.vipannotate.util.DoubleCodec;

/** hg38.phyloP100way.bed.gz: min=-20.0 max=10.003 */
public class PhyloPAnnotationDataCodec {
  static final double SCORE_MIN = -20.0d;
  static final double SCORE_MAX = 10.003d;

  private final DoubleCodec doubleCodec;

  public PhyloPAnnotationDataCodec() {
    this(new DoubleCodec());
  }

  PhyloPAnnotationDataCodec(DoubleCodec doubleCodec) {
    this.doubleCodec = doubleCodec;
  }

  public short encode(Double score) {
    return doubleCodec.encodeDoubleAsShort(score, SCORE_MIN, SCORE_MAX);
  }

  public @Nullable Double decode(short encodedScore) {
    return doubleCodec.decodeDoubleFromShort(encodedScore, SCORE_MIN, SCORE_MAX);
  }
}
