package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToShortEncoder;

public class PhyloPAnnotationEncoder extends IndexedDoubleValueAnnotationToShortEncoder {
  public PhyloPAnnotationEncoder() {
    super(PhyloPAnnotationDataCodec.SCORE_MIN, PhyloPAnnotationDataCodec.SCORE_MAX);
  }
}
