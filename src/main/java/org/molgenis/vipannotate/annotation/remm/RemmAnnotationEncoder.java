package org.molgenis.vipannotate.annotation.remm;

import org.molgenis.vipannotate.annotation.IndexedDoubleValueAnnotationToByteEncoder;

public class RemmAnnotationEncoder extends IndexedDoubleValueAnnotationToByteEncoder {
  public RemmAnnotationEncoder() {
    super(0d, 1d);
  }
}
