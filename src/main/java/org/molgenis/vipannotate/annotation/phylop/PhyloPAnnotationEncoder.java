package org.molgenis.vipannotate.annotation.phylop;


import org.molgenis.vipannotate.annotation.ContigPosDoubleAsShortAnnotationEncoder;

public class PhyloPAnnotationEncoder extends ContigPosDoubleAsShortAnnotationEncoder {
  public PhyloPAnnotationEncoder() {
    super(PhyloPAnnotationDataCodec.SCORE_MIN, PhyloPAnnotationDataCodec.SCORE_MAX);
  }
}
