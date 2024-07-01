package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationFromShortDecoder;
import org.molgenis.vipannotate.util.DoubleCodec;

public class NcERAnnotationDecoder extends IndexedDoubleValueAnnotationFromShortDecoder {
  public NcERAnnotationDecoder(DoubleCodec doubleCodec) {
    super(doubleCodec, NcERAnnotationEncoder.PERC_INTERVAL);
  }
}
