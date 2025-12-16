package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

public class PhyloPAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  /** derived from hg38.phyloP100way.bed.gz */
  static final DoubleInterval SCORE_INTERVAL = new DoubleInterval(-20.0d, 10.003d);

  public PhyloPAnnotationEncoder(DoubleCodec doubleCodec) {
    super(doubleCodec, SCORE_INTERVAL);
  }
}
