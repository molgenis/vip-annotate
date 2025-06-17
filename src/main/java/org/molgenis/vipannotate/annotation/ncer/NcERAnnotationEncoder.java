package org.molgenis.vipannotate.annotation.ncer;

import org.molgenis.vipannotate.annotation.ContigPosDoubleAsShortAnnotationEncoder;

public class NcERAnnotationEncoder extends ContigPosDoubleAsShortAnnotationEncoder {
  public NcERAnnotationEncoder() {
    super(NcERAnnotationDataCodec.PERC_MIN, NcERAnnotationDataCodec.PERC_MAX);
  }
}
