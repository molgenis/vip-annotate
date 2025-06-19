package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;

public class NcERAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  public NcERAnnotationEncoder() {
    super(NcERAnnotationDataCodec.PERC_MIN, NcERAnnotationDataCodec.PERC_MAX);
  }
}
