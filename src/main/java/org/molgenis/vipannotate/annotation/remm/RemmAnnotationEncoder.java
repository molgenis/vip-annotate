package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.annotation.ContigPosDoubleAsByteAnnotationEncoder;

public class RemmAnnotationEncoder extends ContigPosDoubleAsByteAnnotationEncoder {
  public RemmAnnotationEncoder() {
    super(0d, 1d);
  }
}
