package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.util.Encoder;

/** hg38.phyloP100way.bed.gz: min=-20.0 max=10.003 */
public class PhyloPAnnotationDataCodec {
  static final double SCORE_MIN = -20.0d;
  static final double SCORE_MAX = 10.003d;

  public short encode(Double score) {
    return Encoder.encodeDoubleAsShort(score, SCORE_MIN, SCORE_MAX);
  }

  public Double decode(short encodedScore) {
    return Encoder.decodeDoubleFromShort(encodedScore, SCORE_MIN, SCORE_MAX);
  }
}
