package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;
import org.molgenis.vipannotate.util.DoubleCodec;

public class NcERAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  static final int PERC_MIN = 0;
  static final int PERC_MAX = 100;

  public NcERAnnotationEncoder(DoubleCodec doubleCodec) {
    super(doubleCodec, PERC_MIN, PERC_MAX);
  }
}
