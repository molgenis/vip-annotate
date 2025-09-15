package org.molgenis.vipannotate.annotation.phylop;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationFromShortDecoder;
import org.molgenis.vipannotate.util.DoubleCodec;

public class PhyloPAnnotationDecoder extends IndexedDoubleValueAnnotationFromShortDecoder {
  public PhyloPAnnotationDecoder(DoubleCodec doubleCodec) {
    super(doubleCodec, PhyloPAnnotationEncoder.SCORE_INTERVAL);
  }
}
