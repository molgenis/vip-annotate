package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToByteEncoder;
import org.molgenis.vipannotate.util.DoubleCodec;
import org.molgenis.vipannotate.util.DoubleInterval;

public class RemmAnnotationEncoder extends IndexedDoubleValueAnnotationToByteEncoder {
  public RemmAnnotationEncoder(DoubleCodec doubleCodec) {
    super(doubleCodec, DoubleInterval.UNIT);
  }
}
