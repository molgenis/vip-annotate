package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;
import org.molgenis.vipannotate.util.DoubleCodec;

public class PhyloPAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  /** derived from hg38.phyloP100way.bed.gz */
  static final double SCORE_MIN = -20.0d;

  /** derived from hg38.phyloP100way.bed.gz */
  static final double SCORE_MAX = 10.003d;

  public PhyloPAnnotationEncoder(DoubleCodec doubleCodec) {
    super(doubleCodec, SCORE_MIN, SCORE_MAX);
  }
}
