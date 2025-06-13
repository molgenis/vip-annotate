package org.molgenis.vipannotate.db.v2;

import org.molgenis.vipannotate.db.exact.Variant;

final class EmptyAnnotationIndex implements AnnotationIndex {
  private static final EmptyAnnotationIndex INSTANCE = new EmptyAnnotationIndex();

  private EmptyAnnotationIndex() {}

  public static EmptyAnnotationIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int findIndex(Variant variant) {
    return -1;
  }
}
