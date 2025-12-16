package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

public class NcERAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  static final DoubleInterval PERC_INTERVAL = new DoubleInterval(0d, 100d);

  public NcERAnnotationEncoder(DoubleCodec doubleCodec) {
    super(doubleCodec, PERC_INTERVAL);
  }
}
